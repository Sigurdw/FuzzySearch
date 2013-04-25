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

import DataStructure.InternalTrieNode;
import DataStructure.TrieNode;
import DocumentModel.EditOperation;
import Query.ISuggestionWrapper;
import Query.PriorityInteractiveSearch.Links.Link;
import Query.PriorityInteractiveSearch.PriorityActiveNode;
import Query.QueryContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class PriorityActiveNodeTest{

    private TrieNode rootNode;
    private QueryContext queryContext;
    private PriorityActiveNode priorityActiveNode;
    private PriorityQueue<Link> linkQueue;

    @Before
    public void makeSimpleTrieIndex(){
        rootNode = new InternalTrieNode();
        /*rootNode.addNewTerm("aa", 3, 1);
        rootNode.addNewTerm("ab", 2, 1);
        rootNode.addNewTerm("bb", 2, 1);*/
        /*queryContext = new QueryContext(rootNode, 2, -1);
        priorityActiveNode = new PriorityActiveNode(queryContext);
        linkQueue = new PriorityQueue<Link>();*/
    }

    @Test
    public void extractLinkTest(){
        queryContext.QueryString.SetQueryString("a");
        priorityActiveNode.extractLinks(linkQueue);
        assert linkQueue.size() == 3;

        Link bestLink = linkQueue.poll();
        assert bestLink.getRank() == 3;
        assert bestLink.isValid(queryContext.QueryString);

        priorityActiveNode = bestLink.useLink(linkQueue);
        assert linkQueue.size() == 2;
        assert priorityActiveNode.getLastEditOperation() == EditOperation.Match;

        queryContext.QueryString.SetQueryString("aa");
        priorityActiveNode.extractLinks(linkQueue);
        assert linkQueue.size() == 5;
    }

    @Test
    public void isExhaustedTest(){
        assert priorityActiveNode.isExhausted();

        queryContext.QueryString.SetQueryString("a");
        assert !priorityActiveNode.isExhausted();

        priorityActiveNode.extractLinks(linkQueue);
        priorityActiveNode = linkQueue.poll().useLink(linkQueue);
        assert priorityActiveNode.isExhausted();

        queryContext.QueryString.SetQueryString("aa");
        assert !priorityActiveNode.isExhausted();
    }

    @Test
    public void getSuggestionsTest(){
        queryContext.QueryString.SetQueryString("a");
        priorityActiveNode.extractLinks(linkQueue);

        priorityActiveNode = linkQueue.poll().useLink(linkQueue);

        ArrayList<ISuggestionWrapper> suggestions = new ArrayList<ISuggestionWrapper>();
        priorityActiveNode.getSuggestions(suggestions, 5, 2);

        Assert.assertEquals(2, suggestions.size());
        Assert.assertEquals("aa", suggestions.get(0).getSuggestion());
        Assert.assertEquals("ab", suggestions.get(1).getSuggestion());
    }

    @Test
    public void getSuggestionsInStepsTest(){
        ArrayList<ISuggestionWrapper> suggestions = new ArrayList<ISuggestionWrapper>();
        priorityActiveNode.getSuggestions(suggestions, 1, 2);

        Assert.assertEquals(1, suggestions.size());
        Assert.assertEquals("aa", suggestions.get(0).getSuggestion());

        priorityActiveNode.getSuggestions(suggestions, 2, 2);
        Assert.assertEquals(3, suggestions.size());
    }

    @Test
    public void getSuggestionsInStepsByRankTest(){
        ArrayList<ISuggestionWrapper> suggestions = new ArrayList<ISuggestionWrapper>();
        priorityActiveNode.getSuggestions(suggestions, 3, 3);

        Assert.assertEquals(1, suggestions.size());
        Assert.assertEquals("aa", suggestions.get(0).getSuggestion());

        priorityActiveNode.getSuggestions(suggestions, 2, 2);
        Assert.assertEquals(3, suggestions.size());
    }

    @Test
    public void getSuggestionsHardCaseTest(){
        rootNode = new InternalTrieNode();
        /*rootNode.addNewTerm("aa", 4, 1);
        rootNode.addNewTerm("ab", 2, 1);
        rootNode.addNewTerm("bb", 3, 1);*/

        /*queryContext = new QueryContext(rootNode, 1, 3);
        priorityActiveNode = new PriorityActiveNode(queryContext);

        ArrayList<ISuggestionWrapper> suggestions = new ArrayList<ISuggestionWrapper>();
        priorityActiveNode.getAvailableSuggestions(suggestions, 3, 0);

        Assert.assertEquals(3, suggestions.size());
        Assert.assertEquals("aa", suggestions.get(0).getSuggestion());
        Assert.assertEquals("bb", suggestions.get(1).getSuggestion());
        Assert.assertEquals("ab", suggestions.get(2).getSuggestion());*/
    }
}
