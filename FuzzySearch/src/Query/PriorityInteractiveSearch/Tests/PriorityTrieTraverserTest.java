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
    private QueryContext queryContext;
    private ArrayList<ISuggestionWrapper> suggestions;

    private static final int NumberOfAllowedEdits = 1;
    private static final int NumberOfNeededSuggestions = 4;

    @Before
    public void setUp(){
        /*TrieNode root = new InternalTrieNode();
        root.addNewTerm("aa", 3, 0);
        root.addNewTerm("ab", 4, 0);
        root.addNewTerm("bb", 3, 0);
        root.addNewTerm("ba", 3, 0);

        queryContext = new QueryContext(root, NumberOfAllowedEdits, NumberOfNeededSuggestions);

        priorityTrieTraverser = new PriorityTrieTraverser(queryContext);*/
    }
    /*
    @Test
    public void getSuggestionsOnEmptyQueryStringTest(){
        suggestions = priorityTrieTraverser.addCharacter();
        Assert.assertEquals(0, suggestions.size());
    }

    @Test
    public void addCharacterTest(){
        queryContext.QueryString.SetQueryString("a");
        suggestions = priorityTrieTraverser.addCharacter();
        Assert.assertEquals(NumberOfNeededSuggestions, suggestions.size());

        queryContext.QueryString.SetQueryString("aa");
        suggestions = priorityTrieTraverser.addCharacter();
        System.out.println(suggestions);
        Assert.assertEquals(3, suggestions.size());
        Assert.assertEquals("aa", suggestions.get(0).getSuggestion());
        Assert.assertEquals("ab", suggestions.get(1).getSuggestion());
        Assert.assertEquals("ba", suggestions.get(2).getSuggestion());
    }*/
}
