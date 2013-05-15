package Query.PrefixBasedInteractiveSearch;

import Clustering.ClusteringVector;
import DataStructure.LeafTrieNode;
import DataStructure.TrieNode;
import Query.QueryContext;

import java.util.PriorityQueue;

/**
 * User: Sigurd Wien
 * Date: 11.05.13
 * Time: 14:48
 */
public class PrefixActiveNode extends AbstractPrefixActiveNode {
    private final QueryContext queryContext;
    private final TrieNode queryPosition;
    private final TrieNode[] previousTerms;
    private final ClusteringVector clusteringDiscount;
    private final String currentPrefix;
    private final int queryStringPosition;

    private final float editDiscount;
    private final int[] col;
    private final int[] row;

    private int nextChild = 0;

    public PrefixActiveNode(QueryContext queryContext, int clusterId){
        this(
                queryContext,
                queryContext.Index.getIndexCluster(clusterId),
                new TrieNode[0],
                "",
                0,
                ClusteringVector.defaultValue(queryContext.Index.indexHeader.numberOfClusters),
                1.0f / queryContext.getMaxRank(),
                new int[0],
                new int[0]);
    }

    private PrefixActiveNode(
            QueryContext queryContext,
            TrieNode queryPosition,
            TrieNode[] previousTerms,
            String currentPrefix,
            int queryStringIndex,
            ClusteringVector clusteringDiscount,
            float previousEditDiscount,
            int[] previousCol,
            int[] previousRow)
    {

        this.queryContext = queryContext;
        this.queryPosition = queryPosition;
        this.previousTerms = previousTerms;
        this.currentPrefix = currentPrefix;
        this.queryStringPosition = queryStringIndex;
        this.clusteringDiscount = clusteringDiscount;

        row = new int[currentPrefix.length() + 1];
        col = new int[queryStringIndex + 1];
        row[0] = queryStringIndex;
        col[0] = 0;
        if(row.length > 1){
            char rowChar = queryContext.QueryString.GetCharacter(queryStringIndex - 1);
            processDistanceArray(rowChar, currentPrefix, previousRow, row);

            char colChar = currentPrefix.charAt(currentPrefix.length() - 1);
            processDistanceArray(colChar, queryContext.QueryString.getString(), previousCol, col);

            int minEditDistance;
            if(rowChar == colChar){
                minEditDistance = previousCol[previousCol.length - 1];
            }
            else{
                minEditDistance = Math.min(
                        Math.min(row[row.length - 2], col[col.length - 2]),
                        previousCol[previousCol.length - 1]) + 1;
            }

            row[row.length - 1] = minEditDistance;
            col[col.length - 1] = minEditDistance;

            float discount = previousEditDiscount;
            for(int i = 0; i < row[row.length - 1] - previousRow[previousRow.length - 1]; i++){
                discount *= 0.25f;
            }

            editDiscount = discount;
        }
        else{
            editDiscount = previousEditDiscount;
        }
    }

    private void processDistanceArray(
            char arrayChar,
            String currentString,
            int[] previousArray,
            int[] currentArray)
    {
        for (int i = 1; i < currentArray.length - 1; i++){
            if(arrayChar == currentString.charAt(i - 1)){
                currentArray[i] = previousArray[i - 1];
            }
            else{
                currentArray[i] = Math.min(Math.min(previousArray[i - 1], previousArray[i]), currentArray[i - 1]) + 1;
            }
        }
    }

    @Override
    public void getNextChildNodes(final PriorityQueue<AbstractPrefixActiveNode> nodeQueue) {
        TrieNode nextChildNode = queryPosition.getSortedChild(nextChild);
        if(nextChildNode instanceof LeafTrieNode){
            addDeleteSpaceNode(nodeQueue, nextChildNode);
            for(int i = 0; i < queryContext.getNumberOfClusters(); i++){
                TrieNode indexClusterRoot = queryContext.getIndexCluster(i);
                PrefixActiveNode nextChildActiveNode = new PrefixActiveNode(
                        queryContext,
                        indexClusterRoot,
                        getNextTermStack(nextChildNode),
                        currentPrefix + nextChildNode.label,
                        queryStringPosition + 1,
                        clusteringDiscount,
                        getTermCorrelationNormalizedDiscount(),
                        col,
                        row);
                nodeQueue.add(nextChildActiveNode);
            }
        }
        else{
            PrefixActiveNode nextChildActiveNode = new PrefixActiveNode(
                    queryContext,
                    nextChildNode,
                    previousTerms,
                    currentPrefix + nextChildNode.label,
                    queryStringPosition + 1,
                    clusteringDiscount,
                    editDiscount,
                    col,
                    row);
            nodeQueue.add(nextChildActiveNode);
        }

        nextChild++;
    }

    private void addDeleteSpaceNode(final PriorityQueue<AbstractPrefixActiveNode> nodeQueue, TrieNode leafNode) {
        DeleteSpaceActiveNode deleteSpaceNode = new DeleteSpaceActiveNode(
                queryContext,
                leafNode,
                previousTerms,
                queryStringPosition,
                editDiscount * queryContext.EditDiscount);
        nodeQueue.add(deleteSpaceNode);
    }

    private TrieNode[] getNextTermStack(TrieNode termNode) {
        TrieNode[] termStack;
        termStack = new TrieNode[previousTerms.length + 1];
        System.arraycopy(previousTerms, 0, termStack, 0, previousTerms.length);
        termStack[termStack.length - 1] = termNode;
        return termStack;
    }

    private float getTermCorrelationNormalizedDiscount() {
        return editDiscount * queryPosition.getRank() / queryContext.getMaxRank();
    }

    @Override
    public boolean hasMoreChildren() {
        return nextChild < queryPosition.getNumberOfChildren();
    }

    @Override
    public PrefixSuggestionTraverser getSuggestionTraverser(){
        return new PrefixSuggestionTraverser(queryPosition, previousTerms, editDiscount);
    }

    @Override
    public float getRank(){
        return queryPosition.getSortedChild(nextChild).getRank() * editDiscount;
    }

    @Override
    public boolean isExhausted(){
        return currentPrefix.length() == queryContext.QueryString.GetLength()
                || queryPosition instanceof LeafTrieNode;
    }
}
