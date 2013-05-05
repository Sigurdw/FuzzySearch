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

import Config.SearchConfig;
import DataStructure.Index;
import Interface.IUpdateInterfaceControl;
import Interface.WorkingStatus;
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
    private String activeQueryString = "";
    private IndexTraverser query;
    private int numberOfRetrievedSuggestion;
    private SearchConfig searchConfig;


    public QueryWorker(IUpdateInterfaceControl interfaceControl) throws Exception{
        this.interfaceControl = interfaceControl;
        workerThread = new Thread(this);

        index = Index.read(new DataInputStream(new FileInputStream(new File(indexPath))));
        System.out.println("index loaded");
        ArrayList<ISuggestionWrapper> terms = index.getAllTerms();
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
        if(lastCharacter != 0){
            query.initiateFromExhaustedNodes();
            while (!query.isQueryExhausted() && needMoreSuggestion()){
                query.exploreNextNode();
                while(needMoreSuggestion() && query.hasAvailableSuggestions()){
                    getNextAvailableSuggestion();
                }
            }
        }
    }

    private void getNextAvailableSuggestion() {
        ISuggestionWrapper retrievedSuggestion = query.getNextAvailableSuggestion();
        if(retrievedSuggestion != null){
            interfaceControl.addSuggestion(retrievedSuggestion.toString());
            numberOfRetrievedSuggestion++;

            if(!needMoreSuggestion()){
                interfaceControl.updateWorkingStatus(WorkingStatus.CalculatingMoreActiveNodes);
            }
        }
    }

    private boolean needMoreSuggestion() {
        return numberOfRetrievedSuggestion < queryContext.NeededSuggestions;
    }

    private void checkForQueryStringUpdate(){
        boolean needToRestart = false;

        SearchConfig newConfig = queryUpdateQueue.getSearchConfigUpdate();
        if(newConfig != null){
            needToRestart = true;
            searchConfig = newConfig;
        }

        String updatedQueryString = queryUpdateQueue.getQueryStringUpdate();
        if(updatedQueryString != null){
            if(!updatedQueryString.equals(activeQueryString)){
                interfaceControl.updateWorkingStatus(WorkingStatus.LookingForFirstSuggestions);
                interfaceControl.clearSuggestions();
                numberOfRetrievedSuggestion = 0;

                needToRestart = needToRestart || !updatedQueryString.startsWith(activeQueryString);
                if(needToRestart){
                    initInteractiveSearch();
                }

                queryContext.QueryString.SetQueryString(updatedQueryString);
                activeQueryString = updatedQueryString;
            }
        }
    }

    public void initiateConfigUpdate(SearchConfig newConfig) {
        queryUpdateQueue.updateSearchConfig(newConfig);
    }
}
