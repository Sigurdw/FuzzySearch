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

public final class TermStack {

    private final TrieNode[] termStack;

    public TermStack(TrieNode[] termStack){
        this.termStack = termStack;
    }

    @Override
    public boolean equals(Object other){
        TermStack otherTermStack = (TermStack)other;
        if(otherTermStack.termStack.length == termStack.length){
            for(int i = 0; i < termStack.length; i++){
                if(otherTermStack.termStack[i] != termStack[i]){
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public int hashCode(){
        int code = 0;
        for(int i = 0; i < termStack.length; i++){
            code += termStack[i].hashCode();
        }

        return code;
    }
}
