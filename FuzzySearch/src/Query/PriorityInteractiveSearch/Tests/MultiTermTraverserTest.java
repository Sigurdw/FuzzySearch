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
import Query.ISuggestionWrapper;
import Query.PriorityInteractiveSearch.PriorityTrieTraverser;
import Query.QueryContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class MultiTermTraverserTest {
    private PriorityTrieTraverser priorityTrieTraverser;
    private QueryContext queryContext;
    private ArrayList<ISuggestionWrapper> suggestions;

    private static final int NumberOfAllowedEdits = 1;
    private static final int NumberOfNeededSuggestions = 4;

    @Before
    public void setUp(){
        /*TrieNode root = new InternalTrieNode();
        root.addNewTerm("aa", 4, 0);
        root.addNewTerm("ab", 3, 0);
        root.addNewTerm("bb", 2, 0);
        root.addNewTerm("ba", 1, 0);

        queryContext = new QueryContext(root, NumberOfAllowedEdits, NumberOfNeededSuggestions);

        priorityTrieTraverser = new PriorityTrieTraverser(queryContext);*/
    }
    /*
    @Test
    public void multiTermTest(){
        queryContext.QueryString.SetQueryString("aa ab");
        suggestions = priorityTrieTraverser.addCharacter();
        System.out.println(suggestions);
        Assert.assertEquals(4, suggestions.size());
        Assert.assertEquals("aa ab", suggestions.get(0).getSuggestion());
        Assert.assertEquals("aa aa", suggestions.get(1).getSuggestion());
        Assert.assertEquals("ab ab", suggestions.get(2).getSuggestion());
        Assert.assertEquals("aa bb", suggestions.get(3).getSuggestion());
    }

    @Test
    public void multiTermForWorstTest(){
        queryContext.QueryString.SetQueryString("ba aa");
        suggestions = priorityTrieTraverser.addCharacter();
        System.out.println(suggestions);
        Assert.assertEquals(4, suggestions.size());
        Assert.assertEquals("aa aa", suggestions.get(0).getSuggestion());
    }

    @Test
    public void stepwiseMultiTermTest(){
        queryContext.QueryString.SetQueryString("a");
        suggestions = priorityTrieTraverser.addCharacter();

        Assert.assertEquals(4, suggestions.size());
        Assert.assertEquals("aa", suggestions.get(0).getSuggestion());
        Assert.assertEquals("ab", suggestions.get(1).getSuggestion());
        Assert.assertEquals("bb", suggestions.get(2).getSuggestion());
        Assert.assertEquals("ba", suggestions.get(3).getSuggestion());

        queryContext.QueryString.SetQueryString("aa");
        suggestions = priorityTrieTraverser.addCharacter();

        Assert.assertEquals(3, suggestions.size());
        Assert.assertEquals("aa", suggestions.get(0).getSuggestion());
        Assert.assertEquals("ab", suggestions.get(1).getSuggestion());
        Assert.assertEquals("ba", suggestions.get(2).getSuggestion());

        queryContext.QueryString.SetQueryString("aa ");
        suggestions = priorityTrieTraverser.addCharacter();

        System.out.println(suggestions);
        Assert.assertEquals(4, suggestions.size());
        Assert.assertEquals("aa aa", suggestions.get(0).getSuggestion());
        Assert.assertEquals("aa ab", suggestions.get(1).getSuggestion());
        Assert.assertEquals("aa bb", suggestions.get(2).getSuggestion());
        Assert.assertEquals("aa", suggestions.get(3).getSuggestion());
    }

    @Test
    public void tripleTermTest(){
        queryContext.QueryString.SetQueryString("bb aa ab");
        suggestions = priorityTrieTraverser.addCharacter();

        System.out.println(suggestions);
        Assert.assertEquals(4, suggestions.size());
        Assert.assertEquals("bb aa ab", suggestions.get(0).getSuggestion());
        Assert.assertEquals("ab aa ab", suggestions.get(1).getSuggestion());
        Assert.assertEquals("bb aa aa", suggestions.get(2).getSuggestion());
        Assert.assertEquals("bb ab ab", suggestions.get(3).getSuggestion());
    }*/
}
