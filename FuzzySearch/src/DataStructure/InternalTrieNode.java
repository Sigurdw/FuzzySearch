package DataStructure;

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

import Clustering.ClusteringVector;
import Clustering.TermDocumentVector;
import Query.ISuggestionWrapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class InternalTrieNode extends TrieNode {

    private final ArrayList<TrieNode> childrenSortedByRank;

    public static char RootLabel = 1;

    public InternalTrieNode(){
        this(RootLabel);
    }

    public InternalTrieNode(char label) {
        super(label);
        childrenSortedByRank = new ArrayList<TrieNode>();
    }

    public InternalTrieNode(DataInputStream trieReader, float rank, int depth, char[] termBuffer)
        throws IOException
    {
        super(termBuffer[depth], rank);

        int numberOfChildren = trieReader.readInt();
        childrenSortedByRank = new ArrayList<TrieNode>(numberOfChildren);
        for (int i = 0; i < numberOfChildren; i++){
            childrenSortedByRank.add(TrieNode.read(trieReader, depth + 1, termBuffer));
        }
    }

    @Override
    public int getNumberOfChildren() {
        return childrenSortedByRank.size();
    }

    @Override
    public TrieNode getSortedChild(int index) {
        return childrenSortedByRank.get(index);
    }

    @Override
    public int getMatchChildIndex(char characterToMatch) {
        return getChildIndex(characterToMatch);
    }

    @Override
    protected void addNewTerm(String term, TermDocumentVector termDocumentVector, ClusteringVector clusteringVector, int depth) {
        char currentChar;
        if(depth == term.length()){
            currentChar = LeafTrieNode.LeafCharacter;
        }
        else{
            currentChar = getCurrentChar(term, depth);
        }

        int childIndex = getChildIndex(currentChar);
        TrieNode child = getChild(currentChar, childIndex, term, depth);
        child.addNewTerm(term, termDocumentVector, clusteringVector, depth + 1);

        putNodeInOrder(childIndex, child);
        updateNodeRank();
    }

    private char getCurrentChar(String term, int depth) {
        char currentChar;
        if(depth == term.length()){
            currentChar = 0;
        }
        else{
            currentChar = term.charAt(depth);
        }

        return currentChar;
    }

    private TrieNode getChild(char currentChar, int childIndex, String term, int depth) {
        TrieNode child;

        boolean noChildFound = childrenSortedByRank.size() == childIndex;

        if(noChildFound){
            if(depth == term.length()){
                child = new LeafTrieNode(term);
            }
            else{
                child = new InternalTrieNode(currentChar);
            }

            childrenSortedByRank.add(child);
        }
        else{
            child = childrenSortedByRank.get(childIndex);
        }
        return child;
    }

    private void updateNodeRank() {
        rank = childrenSortedByRank.get(0).getRank();
    }

    private void putNodeInOrder(int childIndex, TrieNode child) {
        int targetIndex = getSortedIndex(child);

        if(targetIndex - 1 > childIndex){
            for(int i = childIndex; i < targetIndex - 1; i++){
                TrieNode trieNode = childrenSortedByRank.get(i + 1);
                childrenSortedByRank.set(i, trieNode);
            }

            childrenSortedByRank.set(targetIndex - 1, child);
        }
        else if(targetIndex < childIndex){
            for(int i = childIndex; i > targetIndex; i--){
                TrieNode trieNode = childrenSortedByRank.get(i - 1);
                childrenSortedByRank.set(i, trieNode);
            }

            childrenSortedByRank.set(targetIndex, child);
        }

        validateOrder();
    }

    private int getSortedIndex(TrieNode child) {
        for(int i = 0; i < childrenSortedByRank.size(); i++){
            if(child.getRank() > childrenSortedByRank.get(i).getRank()){
                return i;
            }
            else if(
                i < childrenSortedByRank.size() - 1 && child == childrenSortedByRank.get(i)
                && child.getRank() >= childrenSortedByRank.get(i + 1).getRank())
            {
                return i;
            }
        }

        return childrenSortedByRank.size();
    }

    private void validateOrder(){
        for(int i = 1; i < childrenSortedByRank.size(); i++){
            if(childrenSortedByRank.get(i - 1).getRank() < childrenSortedByRank.get(i).getRank()){
                System.out.println("ERROR!");
            }
        }
    }

    private int getChildIndex(char c) {
        for(int i = 0; i < childrenSortedByRank.size(); i++){
            TrieNode trieNode = childrenSortedByRank.get(i);
            if(trieNode.label == c){
                return i;
            }
        }

        return childrenSortedByRank.size();
    }

    @Override
    protected void writeExtended(DataOutputStream trieWriter) throws IOException {
        trieWriter.writeInt(childrenSortedByRank.size());
        for(TrieNode trieNode : childrenSortedByRank){
            trieNode.write(trieWriter);
        }
    }

    @Override
    protected void getAllTerms(ArrayList<ISuggestionWrapper> suggestionList) {
        for(TrieNode trieNode : childrenSortedByRank){
            trieNode.getAllTerms(suggestionList);
        }
    }

    @Override
    public String toString(){
        return label + ", " + rank;
    }
}
