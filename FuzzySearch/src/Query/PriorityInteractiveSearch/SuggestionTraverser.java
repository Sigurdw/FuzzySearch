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

import DataStructure.TrieNode;
import Query.ISuggestionWrapper;

import java.util.ArrayList;
import java.util.PriorityQueue;

public final class SuggestionTraverser implements Comparable<SuggestionTraverser>{
    private final PriorityQueue<SuggestionNode> suggestionNodeQueue = new PriorityQueue<SuggestionNode>();
    private final TrieNode[] previousTerms;
    private final SuggestionNodeRegister suggestionNodeRegister;
    private final float editDiscount;

    public SuggestionTraverser(
            TrieNode rootNode,
            SuggestionNodeRegister suggestionNodeRegister,
            TrieNode[] previousTerms,
            float editDiscount)
    {
        this.suggestionNodeRegister = suggestionNodeRegister;
        this.previousTerms = previousTerms;
        this.editDiscount = editDiscount;
        SuggestionNode suggestionNode = suggestionNodeRegister.getSuggestionNode(previousTerms, rootNode);

        if (suggestionNode != null){
            suggestionNodeQueue.add(suggestionNode);
        }
    }

    public void getSuggestions(
        final ArrayList<ISuggestionWrapper> suggestions,
        int numberOfSuggestions,
        final float lowerRankLimit)
    {
        while(needMoreSuggestions(numberOfSuggestions) && hasGoodEnoughSuggestions(lowerRankLimit))
        {
            SuggestionNode suggestionNode = suggestionNodeQueue.poll();
            if(suggestionNode.isLeaf()){
                suggestions.add(suggestionNode.getSuggestion(editDiscount, previousTerms));
                numberOfSuggestions--;
            }
            else{
                SuggestionNode nextSuggestionNodeChild = null;
                while(nextSuggestionNodeChild == null && suggestionNode.hasMoreChildren()){
                    TrieNode nextChild = suggestionNode.getNextChild();
                    nextSuggestionNodeChild = suggestionNodeRegister.getSuggestionNode(previousTerms, nextChild);
                }

                if(nextSuggestionNodeChild != null){
                    suggestionNodeQueue.add(nextSuggestionNodeChild);
                }

                if(suggestionNode.hasMoreChildren()){
                    suggestionNodeQueue.add(suggestionNode);
                }
            }
        }
    }

    public ISuggestionWrapper getNextSuggestion(float lowerRankThreshold){
        ISuggestionWrapper suggestionWrapper = null;
        while(hasGoodEnoughSuggestions(lowerRankThreshold))
        {
            SuggestionNode suggestionNode = suggestionNodeQueue.poll();
            if(suggestionNode.isLeaf()){
                suggestionWrapper = suggestionNode.getSuggestion(editDiscount, previousTerms);
                break;
            }
            else{
                SuggestionNode nextSuggestionNodeChild = null;
                while(nextSuggestionNodeChild == null && suggestionNode.hasMoreChildren()){
                    TrieNode nextChild = suggestionNode.getNextChild();
                    nextSuggestionNodeChild = suggestionNodeRegister.getSuggestionNode(previousTerms, nextChild);
                }

                if(nextSuggestionNodeChild != null){
                    suggestionNodeQueue.add(nextSuggestionNodeChild);
                }

                if(suggestionNode.hasMoreChildren()){
                    suggestionNodeQueue.add(suggestionNode);
                }
            }
        }

        return suggestionWrapper;
    }

    public float getNextRank(){
        SuggestionNode suggestionNode = suggestionNodeQueue.peek();
        if(suggestionNode != null){
            return suggestionNode.getNextRank();
        }

        return -2;
    }

    private boolean hasGoodEnoughSuggestions(double lowerRankLimit) {
        SuggestionNode suggestionNode = suggestionNodeQueue.peek();
        return suggestionNode != null && suggestionNode.getNextRank() >= lowerRankLimit;
    }

    private boolean needMoreSuggestions(int numberOfSuggestions) {
        return numberOfSuggestions > 0;
    }

    @Override
    public int compareTo(SuggestionTraverser o) {
        float difference = o.getNextRank() - this.getNextRank();
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
