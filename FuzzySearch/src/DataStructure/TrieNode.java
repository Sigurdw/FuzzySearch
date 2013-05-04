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

public abstract class TrieNode {

    public final char label;

    protected float rank;

    public TrieNode(char label){

        this.label = label;
        rank = -1;
    }

    public TrieNode(char label, float rank){
        this.label = label;
        this.rank = rank;
    }

    public float getRank(){
        return rank;
    }

    public abstract int getNumberOfChildren();

    public abstract TrieNode getSortedChild(int index);

    public abstract int getMatchChildIndex(char characterToMatch);

    public void addNewTerm(String term, TermDocumentVector termDocumentVector, ClusteringVector clusteringVector){
        addNewTerm(term, termDocumentVector, clusteringVector, 0);
    }

    protected abstract void addNewTerm(
            String term,
            TermDocumentVector termDocumentVector,
            ClusteringVector clusteringVector,
            int depth);

    public void write(DataOutputStream trieWriter) throws IOException {
        trieWriter.writeChar(label);
        trieWriter.writeFloat(rank);
        writeExtended(trieWriter);
    }

    protected abstract void writeExtended(DataOutputStream trieWriter) throws IOException;

    public static TrieNode read(DataInputStream trieReader) throws IOException {
        return read(trieReader, 0, new char[256]);
    }

    protected static TrieNode read(DataInputStream trieReader, int depth, char[] termBuffer)
        throws IOException
    {
        termBuffer[depth] = trieReader.readChar();
        float rank = trieReader.readFloat();
        TrieNode trieNode;
        if(termBuffer[depth] == LeafTrieNode.LeafCharacter){
            trieNode = new LeafTrieNode(trieReader, rank, depth, termBuffer);
        }
        else{
            trieNode = new InternalTrieNode(trieReader, rank, depth, termBuffer);
        }

        return trieNode;
    }

    public ArrayList<ISuggestionWrapper> getAllTerms(){
        ArrayList<ISuggestionWrapper> terms = new ArrayList<ISuggestionWrapper>();
        getAllTerms(terms);
        return terms;
    }

    protected abstract void getAllTerms(ArrayList<ISuggestionWrapper> suggestionList);

    @Override
    public String toString(){
        return "" + label;
    }
}
