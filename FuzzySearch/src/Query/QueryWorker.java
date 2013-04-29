package Query;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import DataStructure.Index;
import Interface.IUpdateInterfaceControl;
import Interface.WorkingStatus;
import Query.PriorityInteractiveSearch.IndexTraverser;
import Query.PriorityInteractiveSearch.PriorityTrieTraverser;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class QueryWorker implements Runnable{
    private final Thread workerThread;
    private final IUpdateInterfaceControl interfaceControl;
    private final QueryUpdateQueue queryUpdateQueue = new QueryUpdateQueue();
    private QueryContext queryContext;
    private Index index;
    private String activeQueryString = "";
    private IndexTraverser query;
    private static final String indexPath = "C:/Index/clusteredIndex.dat";


    public QueryWorker(IUpdateInterfaceControl interfaceControl) throws Exception{
        this.interfaceControl = interfaceControl;
        workerThread = new Thread(this);

        index = Index.read(new DataInputStream(new FileInputStream(new File(indexPath))));
        System.out.println("index loaded");
        ArrayList<SuggestionWrapper> terms = index.getAllTerms();
        System.out.println("number of terms: " + terms.size());

        initInteractiveSearch();
    }

    public void startWorker(){
        workerThread.start();
    }

    @Override
    public void run() {
        while(true){
            try{
                queryUpdateQueue.awaitNextQueryStringUpdate();
                doWork();
                interfaceControl.updateWorkingStatus(WorkingStatus.IterationExhausted);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateQueryString(String queryString){
        queryUpdateQueue.updateQueryString(queryString);
    }

    private void initInteractiveSearch() {
        queryContext = new QueryContext(index, 3, 10);
        query = new PriorityTrieTraverser(queryContext);
    }

    private void doWork(){
        checkForQueryStringUpdate();
        char lastCharacter = queryContext.QueryString.GetLastCharacter();
        if(lastCharacter == 0){
            return;
        }

        query.initiateFromExhaustedNodes();

        int numberOfRetrievedSuggestion = query.numberOfRetrievedSuggestions();
        while (!query.isQueryExhausted() && numberOfRetrievedSuggestion < queryContext.NeededSuggestions){

            query.exploreNextNode();
            if(query.hasAvailableSuggestions()){
                ArrayList<SuggestionWrapper> retrievedSuggestions = query.getAvailableSuggestions(
                        queryContext.NeededSuggestions - numberOfRetrievedSuggestion);
                if(numberOfRetrievedSuggestion < retrievedSuggestions.size()){
                    updateSuggestionUi(retrievedSuggestions);

                    if(retrievedSuggestions.size() == queryContext.NeededSuggestions){
                        interfaceControl.updateWorkingStatus(WorkingStatus.CalculatingMoreActiveNodes);
                    }
                    else{
                        interfaceControl.updateWorkingStatus(WorkingStatus.LookingForMoreSuggestions);
                    }
                }

                numberOfRetrievedSuggestion = retrievedSuggestions.size();
            }

        }
    }

    private void checkForQueryStringUpdate(){
        String updatedQueryString = queryUpdateQueue.getQueryStringUpdate();
        if(updatedQueryString != null){
            if(!updatedQueryString.equals(activeQueryString)){
                interfaceControl.updateWorkingStatus(WorkingStatus.LookingForFirstSuggestions);
                clearSuggestionUi();

                if(!updatedQueryString.startsWith(activeQueryString)){
                    initInteractiveSearch();
                }

                queryContext.QueryString.SetQueryString(updatedQueryString);
                activeQueryString = updatedQueryString;
            }
        }
    }

    private void updateSuggestionUi(ArrayList<SuggestionWrapper> suggestions){
        ArrayList<String> processedSuggestions = new ArrayList<String>(suggestions.size());
        for(SuggestionWrapper suggestionWrapper : suggestions){
            processedSuggestions.add(suggestionWrapper.toString());
        }

        interfaceControl.updateSuggestionList(processedSuggestions);
    }

    private void clearSuggestionUi(){
        interfaceControl.updateSuggestionList(new ArrayList<String>(0));
    }
}
