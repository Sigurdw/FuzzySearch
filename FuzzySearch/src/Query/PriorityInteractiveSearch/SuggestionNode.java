package Query.PriorityInteractiveSearch;

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

import DataStructure.LeafTrieNode;
import DataStructure.TrieNode;
import Query.ISuggestionWrapper;
import Query.SuggestionWrapper;

public class SuggestionNode implements Comparable<SuggestionNode> {
    private final TrieNode suggestionPosition;
    private int currentChildIndex;

    public SuggestionNode(TrieNode suggestionPosition){
        this.suggestionPosition = suggestionPosition;
        currentChildIndex = 0;
    }

    public TrieNode getNextChild()
    {
        if(currentChildIndex < suggestionPosition.getNumberOfChildren()){
            TrieNode nextChild = suggestionPosition.getSortedChild(currentChildIndex);
            currentChildIndex++;
            return nextChild;
        }

        return null;
    }

    public ISuggestionWrapper getSuggestion(float rankDiscount, final TrieNode[] previousTerms){
        return new SuggestionWrapper(suggestionPosition, previousTerms, rankDiscount);
    }

    protected float getRank(){
        return suggestionPosition.getRank();
    }

    public float getNextRank(){
        if(suggestionPosition instanceof LeafTrieNode){
            return suggestionPosition.getRank();
        }

        return suggestionPosition.getSortedChild(currentChildIndex).getRank();
    }

    public boolean isLeaf() {
        return suggestionPosition instanceof LeafTrieNode;
    }

    public boolean hasMoreChildren(){
        return currentChildIndex < suggestionPosition.getNumberOfChildren();
    }

    @Override
    public int compareTo(SuggestionNode other) {
        double difference = other.getNextRank() - this.getNextRank();
        if(difference > 0){
            return 1;
        }
        else if(difference < 0){
            return -1;
        }
        else{
            return 0;
        }
    }
}
