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
import Query.SuggestionWrapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LeafTrieNode extends TrieNode {
    public static char LeafCharacter = ' ';
    private final String term;
    private TermDocumentVector documentVector;
    private ClusteringVector clusteringVector;

    public LeafTrieNode(String term) {
        super(LeafCharacter);
        this.term = term;
    }

    public LeafTrieNode(DataInputStream dataInputStream, float rank, int depth, char[] termBuffer)
        throws IOException
    {
        super(termBuffer[depth], rank);
        assert label == LeafCharacter;
        term = new String(termBuffer, 1, depth);
        documentVector = TermDocumentVector.read(dataInputStream);
        clusteringVector = ClusteringVector.read(dataInputStream);
    }

    public ClusteringVector getClusteringVector(){
        return clusteringVector;
    }

    public TermDocumentVector documentVector(){
        return documentVector;
    }

    @Override
    public int getNumberOfChildren() {
        return 0;
    }

    @Override
    public TrieNode getSortedChild(int index) {
        throw new IndexOutOfBoundsException("A LeafNode does not contain children");
    }

    @Override
    public int getMatchChildIndex(char characterToMatch) {
        return 0;
    }

    @Override
    protected void addNewTerm(String term, TermDocumentVector termDocumentVector, ClusteringVector clusteringVector, int depth) {
        this.documentVector = termDocumentVector;
        this.clusteringVector = clusteringVector;
        rank = termDocumentVector.getMaxDocRank();
    }

    @Override
    protected void writeExtended(DataOutputStream trieWriter) throws IOException {
        documentVector.write(trieWriter);
        clusteringVector.write(trieWriter);
    }

    @Override
    protected void getAllTerms(ArrayList<ISuggestionWrapper> suggestionList) {
        suggestionList.add(new SuggestionWrapper(term, rank));
    }

    @Override
    public String toString(){
        return term;
    }
}
