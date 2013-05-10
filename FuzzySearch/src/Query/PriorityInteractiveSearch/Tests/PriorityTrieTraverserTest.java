package Query.PriorityInteractiveSearch.Tests;

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

import Clustering.ClusteringVector;
import Clustering.TermDocumentVector;
import Config.SearchConfig;
import DataStructure.Index;
import DataStructure.InternalTrieNode;
import DataStructure.QueryString;
import DataStructure.TrieNode;
import Query.ISuggestionWrapper;
import Query.PriorityInteractiveSearch.PriorityActiveNode;
import Query.PriorityInteractiveSearch.PriorityTrieTraverser;
import Query.QueryContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class PriorityTrieTraverserTest {

    private PriorityTrieTraverser priorityTrieTraverser;
    private Index index;

    @Before
    public void setUp(){
        /*index = new Index(2, 2);
        TermDocumentVector vector = new TermDocumentVector(2);
        vector.setDocument(0, 1.0f, 0);
        vector.setDocument(1, 0.5f, 0);
        ClusteringVector clusteringVector = new ClusteringVector(2, 1);
        clusteringVector.Vector[0] = 1.0f;
        clusteringVector.Vector[1] = 0.5f;

        index.addTerm("protein", vector, clusteringVector);
        vector = new TermDocumentVector(2);
        vector.setDocument(0, 0, 0);
        vector.setDocument(1, 1.1f, 0);
        clusteringVector = new ClusteringVector(2, 0);
        clusteringVector.Vector[0] = 0.1f;
        clusteringVector.Vector[1] = 0.9f;

        index.addTerm("test", vector, clusteringVector);

        vector.setDocument(0, 0, 0);
        vector.setDocument(1, 1.1f, 0);
        clusteringVector = new ClusteringVector(2, 0);
        clusteringVector.Vector[0] = 0.1f;
        clusteringVector.Vector[1] = 0.9f;

        index.addTerm("rotein", vector, clusteringVector);

        vector.setDocument(0, 0, 0);
        vector.setDocument(1, 1.1f, 0);
        clusteringVector = new ClusteringVector(2, 0);
        clusteringVector.Vector[0] = 0.1f;
        clusteringVector.Vector[1] = 0.9f;

        index.addTerm("pro", vector, clusteringVector);

        SearchConfig searchConfig = SearchConfig.DummyConfig;
        searchConfig = searchConfig.updateConfig(index);

        priorityTrieTraverser = new PriorityTrieTraverser(searchConfig);*/

        index = new Index(1, 1);
        TermDocumentVector vector = new TermDocumentVector(2);
        vector.setDocument(0, 1.1f, 0);
        ClusteringVector clusteringVector = new ClusteringVector(1, 0);
        clusteringVector.Vector[0] = 0.1f;

        index.addTerm("pro", vector, clusteringVector);

        SearchConfig searchConfig = SearchConfig.DummyConfig;
        searchConfig = searchConfig.updateConfig(index);

        priorityTrieTraverser = new PriorityTrieTraverser(searchConfig);
    }

    @Test
    public void getSuggestionsOnEmptyQueryStringTest(){
        priorityTrieTraverser.updateQueryString("p");
        priorityTrieTraverser.initiateFromExhaustedNodes();
        ArrayList<ISuggestionWrapper> suggestionWrappers = getSuggestions();
        Assert.assertEquals(1, suggestionWrappers.size());

        priorityTrieTraverser.updateQueryString("p ");
        priorityTrieTraverser.initiateFromExhaustedNodes();
        suggestionWrappers = getSuggestions();
        Assert.assertEquals(1, suggestionWrappers.size());

        priorityTrieTraverser.updateQueryString("p r");
        priorityTrieTraverser.initiateFromExhaustedNodes();
        suggestionWrappers = getSuggestions();
        Assert.assertEquals(1, suggestionWrappers.size());
    }

    @Test
    public void termCorrelationTest(){
        index = new Index(1, 2);
        TermDocumentVector vector = new TermDocumentVector(1);
        vector.setDocument(0, 1.0f, 0);
        ClusteringVector clusteringVector = new ClusteringVector(2, 0);
        clusteringVector.Vector[0] = 1.0f;
        clusteringVector.Vector[1] = 0.5f;
        index.addTerm("pro", vector, clusteringVector);

        vector = new TermDocumentVector(1);
        vector.setDocument(0, 1.0f, 0);
        clusteringVector = new ClusteringVector(2, 1);
        clusteringVector.Vector[0] = 0.5f;
        clusteringVector.Vector[1] = 1.0f;
        index.addTerm("protein", vector, clusteringVector);

        SearchConfig searchConfig = SearchConfig.DummyConfig;
        searchConfig = searchConfig.updateConfig(index);

        priorityTrieTraverser = new PriorityTrieTraverser(searchConfig);

        priorityTrieTraverser.updateQueryString("pro ");
        priorityTrieTraverser.initiateFromExhaustedNodes();
        ArrayList<ISuggestionWrapper> suggestionWrappers = getSuggestions();
        //Assert.assertEquals(2, suggestionWrappers.size());
        printResults(suggestionWrappers);

        priorityTrieTraverser = new PriorityTrieTraverser(searchConfig);

        priorityTrieTraverser.updateQueryString("protein ");
        priorityTrieTraverser.initiateFromExhaustedNodes();
        suggestionWrappers = getSuggestions();
        //Assert.assertEquals(2, suggestionWrappers.size());
        printResults(suggestionWrappers);
    }

    private ArrayList<ISuggestionWrapper> getSuggestions(){
        ArrayList<ISuggestionWrapper> suggestionWrappers = new ArrayList<ISuggestionWrapper>(5);
        while(!priorityTrieTraverser.isQueryExhausted() &&  suggestionWrappers.size() < 5){
            priorityTrieTraverser.exploreNextNode();
            while(priorityTrieTraverser.hasAvailableSuggestions()){
                ISuggestionWrapper suggestionWrapper = priorityTrieTraverser.getNextAvailableSuggestion();
                if(suggestionWrapper != null){
                    suggestionWrappers.add(suggestionWrapper);
                }
            }
        }

        return suggestionWrappers;
    }

    private void printResults(ArrayList<ISuggestionWrapper> suggestionWrappers){
        for(ISuggestionWrapper suggestionWrapper : suggestionWrappers){
            System.out.println(suggestionWrapper);
        }
    }
}
