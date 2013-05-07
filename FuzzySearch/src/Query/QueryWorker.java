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
import Interface.IUpdateInterfaceControl;
import Interface.WorkingStatus;
import Query.PriorityInteractiveSearch.PriorityTrieTraverser;

public class QueryWorker implements Runnable{
    private final Thread workerThread;
    private final IUpdateInterfaceControl interfaceControl;
    private final QueryUpdateQueue queryUpdateQueue = new QueryUpdateQueue();
    private String activeQueryString = "";
    private IndexTraverser query;
    private int numberOfRetrievedSuggestion;
    private SearchConfig searchConfig;

    private boolean started = false;


    public QueryWorker(IUpdateInterfaceControl interfaceControl) throws Exception{
        this.interfaceControl = interfaceControl;
        workerThread = new Thread(this);
    }

    public void startWorker(SearchConfig searchConfig){
        this.searchConfig = searchConfig;
        initInteractiveSearch();
        workerThread.start();
        started = true;
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
        query = new PriorityTrieTraverser(searchConfig);
    }

    private void doWork(){
        boolean gotUpdate = checkForQueryStringUpdate();
        if(activeQueryString.length() > 0 && gotUpdate){
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
        return numberOfRetrievedSuggestion < searchConfig.getNeededSuggestion();
    }

    private boolean checkForQueryStringUpdate(){
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

                query.updateQueryString(updatedQueryString);
                activeQueryString = updatedQueryString;
                return true;
            }
        }

        return false;
    }

    public void initiateConfigUpdate(SearchConfig newConfig) {
        queryUpdateQueue.updateSearchConfig(newConfig);
    }

    public boolean isStarted() {
        return started;
    }
}
