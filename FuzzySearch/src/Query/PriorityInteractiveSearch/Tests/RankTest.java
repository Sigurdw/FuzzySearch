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
import Query.PriorityInteractiveSearch.Links.Link;
import Query.PriorityInteractiveSearch.PriorityActiveNode;
import Query.QueryContext;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.PriorityQueue;

public class RankTest {
    private TrieNode rootNode;
    private QueryContext queryContext;
    private PriorityActiveNode priorityActiveNode;
    private PriorityQueue<Link> linkQueue;

    @Before
    public void makeSimpleTrieIndex(){
        /*rootNode = new InternalTrieNode();
        rootNode.addNewTerm("aa", 3, 1);
        rootNode.addNewTerm("ab", 2, 1);
        rootNode.addNewTerm("bb", 2, 1);
        queryContext = new QueryContext(rootNode, 2, -1);
        priorityActiveNode = new PriorityActiveNode(queryContext);
        linkQueue = new PriorityQueue<Link>();*/
    }

    @Test
    public void getRankTest(){
        queryContext.QueryString.SetQueryString("a");
        priorityActiveNode.extractLinks(linkQueue);

        Link bestLink = linkQueue.poll();
        Assert.assertEquals(3, bestLink.getRank(), 0.0001);

        bestLink = linkQueue.poll();
        Assert.assertEquals(1.5, bestLink.getRank(), 0.25);
    }
}
