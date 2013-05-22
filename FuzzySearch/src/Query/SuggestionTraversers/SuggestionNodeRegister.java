package Query.SuggestionTraversers;

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

import DataStructure.TrieNode;

import java.util.HashMap;

public final class SuggestionNodeRegister {
    private final HashMap<TermStack, HashMap<TrieNode, SuggestionNode>> suggestionNodeRegister
        = new HashMap<TermStack, HashMap<TrieNode, SuggestionNode>>();

    public SuggestionNode getSuggestionNode(TrieNode[] termStack, TrieNode childPosition){
        TermStack key = new TermStack(termStack);

        if(!suggestionNodeRegister.containsKey(key)){
            suggestionNodeRegister.put(key, new HashMap<TrieNode, SuggestionNode>());
        }

        HashMap<TrieNode, SuggestionNode> register = suggestionNodeRegister.get(key);

        if(!register.containsKey(childPosition)){
            SuggestionNode suggestionNode = new SuggestionNode(childPosition);
            register.put(childPosition, suggestionNode);
            return suggestionNode;
        }

        return null;
    }

    public void clearRegister(){
        suggestionNodeRegister.clear();
    }
}
