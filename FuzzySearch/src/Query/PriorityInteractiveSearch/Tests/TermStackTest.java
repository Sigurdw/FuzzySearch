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
import Query.SuggestionTraversers.TermStack;
import junit.framework.Assert;
import org.junit.Test;

public class TermStackTest {

    @Test
    public void TermTestEqualityTest(){
        TrieNode[] termStack = new TrieNode[3];
        for(int i = 0; i < termStack.length; i++){
            termStack[i] = new InternalTrieNode();
        }

        TermStack termStack1 = new TermStack(termStack);
        TermStack termStack2 = new TermStack(termStack);
        TermStack termStack3 = new TermStack(new TrieNode[0]);
        TermStack termStack4 = new TermStack(new TrieNode[0]);

        Assert.assertEquals(termStack1, termStack2);
        Assert.assertEquals(termStack3, termStack4);
        Assert.assertEquals(termStack1.hashCode(), termStack2.hashCode());
        Assert.assertEquals(termStack3.hashCode(), termStack4.hashCode());

        Assert.assertNotSame(termStack1, termStack3);
    }
}
