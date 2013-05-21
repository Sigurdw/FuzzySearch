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

import Clustering.ClusteringVector;
import DataStructure.LeafTrieNode;
import DataStructure.TrieNode;
import DocumentModel.EditOperation;
import Query.PriorityInteractiveSearch.Links.EditLink;
import Query.PriorityInteractiveSearch.Links.Link;
import Query.QueryContext;
import java.util.PriorityQueue;

public class PriorityActiveNode {
    private final TrieNode queryPosition;
    private final TrieNode[] previousTerms;
    private final int previousEdits;
    private final int queryStringIndex;
    private final EditOperation lastEditOperation;
    private final float editDiscount;
    private final boolean isSubstitution;
    private final QueryContext queryContext;
    private final int depth;
    private final ClusteringVector clusteringDiscount;

    private int nextChild = 0;
    private boolean firstAdded = false;
    private int matchIndex = -1;

    public PriorityActiveNode(QueryContext queryContext, int clusterId)
    {
        this(
                queryContext.Index.getIndexCluster(clusterId),
                new TrieNode[0],
                queryContext,
                ClusteringVector.defaultValue(queryContext.Index.indexHeader.numberOfClusters),
                0,
                EditOperation.Match,
                0,
                1.0f / queryContext.getMaxRank(),
                false,
                0);
    }

    private PriorityActiveNode(
        TrieNode queryPosition,
        TrieNode[] previousTerms,
        QueryContext queryContext,
        ClusteringVector clusteringDiscount,
        int previousEdits,
        EditOperation lastEditOperation,
        int queryStringIndex,
        float editDiscount,
        boolean isSubstitution,
        int depth)
    {
        this.queryPosition = queryPosition;
        this.previousTerms = previousTerms;
        this.clusteringDiscount = clusteringDiscount;
        this.previousEdits = previousEdits;
        this.lastEditOperation = lastEditOperation;
        this.queryContext = queryContext;
        this.queryStringIndex = queryStringIndex;
        this.editDiscount = editDiscount;
        this.isSubstitution = isSubstitution;
        this.depth = depth;
    }

    public EditOperation getLastEditOperation(){
        return lastEditOperation;
    }

    public void extractLinks(PriorityQueue<Link> linkQueue){
        if(!firstAdded && !(queryPosition instanceof LeafTrieNode)){
            firstAdded = true;
            addMatchToList(linkQueue);
            addDeleteToList(linkQueue);
            addNextEditsToList(linkQueue);
        }
    }

    private void addMatchToList(PriorityQueue<Link> linkQueue){
        char currentCharacter = queryContext.QueryString.GetCharacter(queryStringIndex);
        matchIndex = queryPosition.getMatchChildIndex(currentCharacter);

        if(matchIndex != queryPosition.getNumberOfChildren()){
            TrieNode matchNode = queryPosition.getSortedChild(matchIndex);
            if(matchNode instanceof LeafTrieNode){
                addDummyDeleteLink(linkQueue, matchNode);
                float matchDiscount = getTermCorrelationNormalizedDiscount(editDiscount);
                TrieNode[] termStack = getNextTermStack(matchNode);
                ClusteringVector termClusterVector = ((LeafTrieNode)matchNode).getClusteringVector();
                ClusteringVector queryClusterVector = termClusterVector.pairwiseMultiply(clusteringDiscount);
                for (int i = 0; i < queryContext.getNumberOfClusters(); i++){
                    TrieNode indexClusterRoot = queryContext.getIndexCluster(i);
                    float discount = matchDiscount * queryClusterVector.Vector[i];
                    PriorityActiveNode indexClusterActiveNode = new PriorityActiveNode(
                            indexClusterRoot,
                            termStack,
                            queryContext,
                            queryClusterVector,
                            previousEdits,
                            EditOperation.Match,
                            queryStringIndex + 1,
                            discount,
                            false,
                            depth + 1);
                    linkQueue.add(new EditLink(this, indexClusterActiveNode));
                }
            }
            else{
                PriorityActiveNode matchActiveNode = new PriorityActiveNode(
                        matchNode,
                        previousTerms,
                        queryContext,
                        clusteringDiscount,
                        previousEdits,
                        EditOperation.Match,
                        queryStringIndex + 1,
                        editDiscount,
                        false,
                        depth + 1);

                linkQueue.add(new EditLink(this, matchActiveNode));
            }
        }
    }

    private void addDummyDeleteLink(PriorityQueue<Link> linkQueue, TrieNode leafNode) {
        int deleteSpaceEditDistance = previousEdits + 1;
        if(deleteSpaceEditDistance <= queryContext.MaxEdits){
            PriorityActiveNode dummyDeleteNode = new PriorityActiveNode(
                    leafNode,
                    previousTerms,
                    queryContext,
                    clusteringDiscount,
                    deleteSpaceEditDistance,
                    EditOperation.Delete,
                    queryStringIndex + 1,
                    editDiscount * queryContext.EditDiscount,
                    true,
                    depth + 1);
            linkQueue.add(new EditLink(this, dummyDeleteNode));
        }
    }

    private TrieNode[] getNextTermStack(TrieNode termNode) {
        TrieNode[] termStack;
        termStack = new TrieNode[previousTerms.length + 1];
        System.arraycopy(previousTerms, 0, termStack, 0, previousTerms.length);
        termStack[termStack.length - 1] = termNode;
        return termStack;
    }

    private float getTermCorrelationNormalizedDiscount(float editDiscount) {
        return editDiscount * queryPosition.getRank() / queryContext.getMaxRank();
    }

    private void addDeleteToList(PriorityQueue<Link> linkQueue){
        if(canDelete()){
            int cost = EditOperation.getOperationCost(lastEditOperation, EditOperation.Delete);
            if(previousEdits + cost <= queryContext.MaxEdits){
                float modifier = 1;
                if(cost != 0){
                    modifier = queryContext.EditDiscount;
                }

                int movement = EditOperation.getOperationMovement(EditOperation.Delete);

                PriorityActiveNode deleteNode = new PriorityActiveNode(
                    queryPosition,
                    previousTerms,
                    queryContext,
                    clusteringDiscount,
                    previousEdits + cost,
                    EditOperation.Delete,
                    queryStringIndex + movement,
                    editDiscount * modifier,
                    cost == 0,
                    depth);

                linkQueue.add(new EditLink(this, deleteNode));
            }
        }
    }

    private boolean canDelete(){
        return EditOperation.isOperationAllowed(lastEditOperation, EditOperation.Delete);
    }

    private void addNextEditsToList(PriorityQueue<Link> linkQueue) {
        if(previousEdits + 1 <= queryContext.MaxEdits){
            if(EditOperation.isOperationAllowed(lastEditOperation, EditOperation.Insert) || isSubstitution){
                TrieNode bestEditNode = getNextEditNode();
                if(bestEditNode != null){
                    float editLinkDiscount = editDiscount * queryContext.EditDiscount;

                    if(bestEditNode instanceof LeafTrieNode){
                        if(queryContext.MultiTerm){
                            addDummyDeleteLink(linkQueue, bestEditNode);
                            editLinkDiscount = getTermCorrelationNormalizedDiscount(editLinkDiscount);
                            TrieNode[] termStack = getNextTermStack(bestEditNode);
                            ClusteringVector termClusterVector = ((LeafTrieNode)bestEditNode).getClusteringVector();
                            ClusteringVector queryClusterVector = termClusterVector.pairwiseMultiply(clusteringDiscount);
                            for (int i = 0; i < queryContext.getNumberOfClusters(); i++){
                                TrieNode indexClusterRoot = queryContext.getIndexCluster(i);
                                float discount = editLinkDiscount * queryClusterVector.Vector[i];
                                PriorityActiveNode indexClusterActiveNode = new PriorityActiveNode(
                                        indexClusterRoot,
                                        termStack,
                                        queryContext,
                                        queryClusterVector,
                                        previousEdits + 1,
                                        EditOperation.Insert,
                                        queryStringIndex,
                                        discount,
                                        false,
                                        depth + 1);
                                linkQueue.add(new EditLink(this, indexClusterActiveNode));
                            }
                        }
                    }
                    else{
                        PriorityActiveNode insertNode = new PriorityActiveNode(
                                bestEditNode,
                                previousTerms,
                                queryContext,
                                clusteringDiscount,
                                previousEdits + 1,
                                EditOperation.Insert,
                                queryStringIndex,
                                editLinkDiscount,
                                false,
                                depth + 1);

                        linkQueue.add(new EditLink(this, insertNode));
                    }
                }
            }
        }
    }

    private TrieNode getNextEditNode() {
        TrieNode bestEditNode = null;
        if(nextChild < queryPosition.getNumberOfChildren()){
            if(nextChild == matchIndex){
                nextChild++;
            }

            if(nextChild < queryPosition.getNumberOfChildren()){
                bestEditNode = queryPosition.getSortedChild(nextChild);
                nextChild++;
            }
        }

        return bestEditNode;
    }

    public void maybeAddNextLink(PriorityQueue<Link> linkQueue){
        addNextEditsToList(linkQueue);
    }

    public float getRank() {
        return queryPosition.getRank() * editDiscount;
    }

    public SuggestionTraverser getSuggestionsTraverser()
    {
        return new SuggestionTraverser(queryPosition, queryContext.SuggestionNodeRegister, previousTerms, editDiscount);
    }

    public boolean isExhausted(){
        return queryContext.QueryString.IsExhausted(queryStringIndex);
    }

    @Override
    public String toString(){
        return queryPosition + " , Edits: " + previousEdits + " , Last operation: " + lastEditOperation + " , Index: " + queryStringIndex;
    }

    public int getDepth(){
        return depth;
    }

    public boolean isSubstitution(){
        return isSubstitution;
    }
}
