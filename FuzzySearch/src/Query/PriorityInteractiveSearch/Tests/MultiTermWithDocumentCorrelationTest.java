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

public class MultiTermWithDocumentCorrelationTest {
    private PriorityTrieTraverser priorityTrieTraverser;
    private QueryContext queryContext;
    private ArrayList<ISuggestionWrapper> suggestions;

    private static final int NumberOfAllowedEdits = 1;
    private static final int NumberOfNeededSuggestions = 2;

    @Before
    public void setUp(){
        /*TrieNode root = new InternalTrieNode();
        root.addNewTerm("aa", 4, 1);
        root.addNewTerm("aa", 4, 0);
        root.addNewTerm("ab", 3, 0);
        root.addNewTerm("ab", 3, 2);
        root.addNewTerm("bb", 2, 1);
        root.addNewTerm("bb", 2, 0);
        root.addNewTerm("ba", 1, 0);

        queryContext = new QueryContext(root, NumberOfAllowedEdits, NumberOfNeededSuggestions);

        query = new PriorityTrieTraverser(queryContext);*/
    }

    @Test
    public void orderSwitchTest(){
        /*queryContext.QueryString.SetQueryString("aa ab");
        suggestions = query.addCharacter();
        System.out.println(suggestions);
        Assert.assertEquals(4, suggestions.size());
        Assert.assertEquals("aa ab", suggestions.get(0).getSuggestion());
        Assert.assertEquals("aa aa", suggestions.get(1).getSuggestion());
        Assert.assertEquals("ab ab", suggestions.get(2).getSuggestion());
        Assert.assertEquals("aa bb", suggestions.get(3).getSuggestion());*/
    }


}
