package Query.SimpleInteractiveSearch;

import Clustering.ClusteringVector;
import DataStructure.LeafTrieNode;
import DataStructure.TrieNode;
import DocumentModel.EditOperation;
import Query.PriorityInteractiveSearch.SuggestionTraverser;
import Query.QueryContext;

import java.util.*;

public class ActiveNode implements Comparable<ActiveNode> {
    private final QueryContext queryContext;
    private final TrieNode queryPosition;
    private final int previousEdits;
    private final EditOperation lastEditOperation;
    private final int queryStringIndex;
    private final boolean substitution;
    private final TrieNode[] previousTerms;
    private final float editDiscount;
    private final ClusteringVector clusteringDiscount;

    public ActiveNode(QueryContext queryContext, int clusterId){
        this(
                queryContext,
                queryContext.getIndexCluster(clusterId),
                0,
                EditOperation.Match,
                0,
                false,
                new TrieNode[0],
                1.0f / queryContext.getMaxRank(),
                ClusteringVector.defaultValue(queryContext.Index.indexHeader.numberOfClusters));
    }

    private ActiveNode(
            QueryContext queryContext,
            TrieNode queryPosition,
            int previousEdits,
            EditOperation lastEditOperation,
            int queryStringIndex,
            final boolean substitution,
            TrieNode[] previousTerms,
            float editDiscount,
            ClusteringVector clusteringDiscount)
    {
        this.queryContext = queryContext;
        this.queryPosition = queryPosition;
        this.previousEdits = previousEdits;
        this.lastEditOperation = lastEditOperation;
        this.queryStringIndex = queryStringIndex;
        this.substitution = substitution;
        this.previousTerms = previousTerms;
        this.editDiscount = editDiscount;
        this.clusteringDiscount = clusteringDiscount;
    }

    public void addCharacter(ArrayList<ActiveNode> activeNodes){
        if(queryPosition instanceof LeafTrieNode){
            return;
        }

        for(int i = 0; i < queryPosition.getNumberOfChildren(); i++){
            TrieNode newQueryPosition = queryPosition.getSortedChild(i);
            char candidatePath = newQueryPosition.label;
            if(candidatePath == queryContext.QueryString.GetCharacter(queryStringIndex)){
                if(newQueryPosition instanceof LeafTrieNode){
                    addDummyDeleteLink(activeNodes, newQueryPosition);
                    float matchDiscount = getTermCorrelationNormalizedDiscount(editDiscount);
                    TrieNode[] termStack = getNextTermStack(newQueryPosition);
                    ClusteringVector termClusterVector = ((LeafTrieNode)newQueryPosition).getClusteringVector();
                    ClusteringVector queryClusterVector = termClusterVector.pairwiseMultiply(clusteringDiscount);
                    for (int j = 0; j < queryContext.getNumberOfClusters(); j++){
                        TrieNode indexClusterRoot = queryContext.getIndexCluster(j);
                        float discount = matchDiscount * queryClusterVector.Vector[j];
                        ActiveNode indexClusterActiveNode = new ActiveNode(
                                queryContext,
                                indexClusterRoot,
                                previousEdits,
                                EditOperation.Match,
                                queryStringIndex + 1,
                                false,
                                termStack,
                                discount,
                                queryClusterVector);
                        activeNodes.add(indexClusterActiveNode);
                    }
                }
                else{
                    activeNodes.add(new ActiveNode(
                            queryContext,
                            newQueryPosition,
                            previousEdits,
                            EditOperation.Match,
                            queryStringIndex + 1,
                            false,
                            previousTerms,
                            editDiscount,
                            clusteringDiscount));
                }
            }
            else{
                 processInsert(newQueryPosition, activeNodes);
            }
        }

        processDeletes(activeNodes);
    }

    private void processInsert(TrieNode newQueryPosition, ArrayList<ActiveNode> activeNodes)
    {
        if(isAllowedToInsert()){
            float insertDiscount = editDiscount * queryContext.EditDiscount;
            if(newQueryPosition instanceof LeafTrieNode){
                addDummyDeleteLink(activeNodes, newQueryPosition);
                insertDiscount = getTermCorrelationNormalizedDiscount(insertDiscount);
                TrieNode[] termStack = getNextTermStack(newQueryPosition);
                ClusteringVector termClusterVector = ((LeafTrieNode)newQueryPosition).getClusteringVector();
                ClusteringVector queryClusterVector = termClusterVector.pairwiseMultiply(clusteringDiscount);
                for (int j = 0; j < queryContext.getNumberOfClusters(); j++){
                    TrieNode indexClusterRoot = queryContext.getIndexCluster(j);
                    float discount = insertDiscount * queryClusterVector.Vector[j];
                    ActiveNode indexClusterActiveNode = new ActiveNode(
                            queryContext,
                            indexClusterRoot,
                            previousEdits + 1,
                            EditOperation.Insert,
                            queryStringIndex,
                            false,
                            termStack,
                            discount,
                            queryClusterVector);
                    indexClusterActiveNode.addCharacter(activeNodes);
                }
            }
            else{
                ActiveNode tempActiveNode = new ActiveNode(
                        queryContext,
                        newQueryPosition,
                        previousEdits + 1,
                        EditOperation.Insert,
                        queryStringIndex,
                        false,
                        previousTerms,
                        editDiscount * queryContext.EditDiscount,
                        clusteringDiscount);
                tempActiveNode.addCharacter(activeNodes);
            }
        }
    }

    private boolean isAllowedToInsert() {
        return (lastEditOperation != EditOperation.Delete || substitution) && previousEdits < queryContext.MaxEdits;
    }

    private void processDeletes(ArrayList<ActiveNode> activeNodes) {
        if(isAllowToDelete()){
            int editCost = EditOperation.getOperationCost(lastEditOperation, EditOperation.Delete);
            boolean isSubstitution  = false;
            float modifier = queryContext.EditDiscount;
            if(editCost == 0){
                isSubstitution = true;
                modifier = 1.0f;
            }

            activeNodes.add(new ActiveNode(
                    queryContext,
                    queryPosition,
                    previousEdits + editCost,
                    EditOperation.Delete,
                    queryStringIndex + 1,
                    isSubstitution,
                    previousTerms,
                    editDiscount * modifier,
                    clusteringDiscount));
        }
    }

    private void addDummyDeleteLink(ArrayList<ActiveNode> activeNodes, TrieNode leafNode) {
        int deleteSpaceEditDistance = previousEdits + 1;
        if(deleteSpaceEditDistance <= queryContext.MaxEdits){
            ActiveNode dummyDeleteNode = new ActiveNode(
                    queryContext,
                    leafNode,
                    deleteSpaceEditDistance,
                    EditOperation.Delete,
                    queryStringIndex + 1,
                    true,
                    previousTerms,
                    editDiscount * queryContext.EditDiscount,
                    clusteringDiscount);
            activeNodes.add(dummyDeleteNode);
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

    private boolean isAllowToDelete() {
        return lastEditOperation == EditOperation.Insert || previousEdits < queryContext.MaxEdits;
    }

    public SuggestionTraverser getSuggestionTraverser(){
        return new SuggestionTraverser(queryPosition, queryContext.SuggestionNodeRegister, previousTerms, editDiscount);
    }

    private float getRank(){
        return queryPosition.getRank() * editDiscount;
    }

    @Override
    public int compareTo(ActiveNode otherNode) {
        float difference = otherNode.getRank() - this.getRank();
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
