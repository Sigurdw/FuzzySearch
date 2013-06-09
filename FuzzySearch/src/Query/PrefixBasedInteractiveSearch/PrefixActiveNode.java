package Query.PrefixBasedInteractiveSearch;

import Clustering.ClusteringVector;
import DataStructure.LeafTrieNode;
import DataStructure.TrieNode;
import Query.QueryContext;
import Query.SuggestionTraversers.PrefixSuggestionTraverser;

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
    private final int neededExtraDepth;
    private final int extraDepth;

    private int nextChild = 0;

    private int matchIndex = -1;

    public PrefixActiveNode(QueryContext queryContext, int clusterId){
        this(
                queryContext,
                queryContext.Index.getIndexCluster(clusterId),
                new TrieNode[0],
                "",
                0,
                ClusteringVector.defaultValue(queryContext.Index.indexHeader.numberOfClusters),
                1.0f / queryContext.getMaxRank(),
                new int[]{0},
                new int[]{0},
                0,
                0);
    }

    private PrefixActiveNode(
            QueryContext queryContext,
            TrieNode queryPosition,
            TrieNode[] previousTerms,
            String currentPrefix,
            int queryStringPosition,
            ClusteringVector clusteringDiscount,
            float editDiscount,
            int[] col,
            int[] row,
            int neededExtraDepth,
            int extraDepth)
    {

        this.queryContext = queryContext;
        this.queryPosition = queryPosition;
        this.previousTerms = previousTerms;
        this.currentPrefix = currentPrefix;
        this.queryStringPosition = queryStringPosition;
        this.clusteringDiscount = clusteringDiscount;
        this.editDiscount = editDiscount;
        this.col = col;
        this.row = row;
        this.neededExtraDepth = neededExtraDepth;
        this.extraDepth = extraDepth;
    }

    @Override
    public void getNextChildNodes(final PriorityQueue<AbstractPrefixActiveNode> nodeQueue) {
        TrieNode nextChildNode;// = queryPosition.getSortedChild(nextChild);
        //nextChild++;
        if(matchIndex == -1){
            matchIndex = queryPosition.getMatchChildIndex(queryContext.QueryString.GetLastCharacter());
            if(matchIndex != queryPosition.getNumberOfChildren()){
                nextChildNode = queryPosition.getSortedChild(matchIndex);
            }
            else{
                nextChildNode = queryPosition.getSortedChild(nextChild);
                nextChild++;
            }
        }
        else{
            nextChildNode = queryPosition.getSortedChild(nextChild);
            nextChild++;
        }

        if(nextChild == matchIndex){
            nextChild++;
        }

        int[] nextCol;
        int[] nextRow;
        int nextQueryStringPosition = queryStringPosition + 1;
        int nextEditDistance;
        int nextExtraDepth = extraDepth;
        int nextNeededExtraDepth = neededExtraDepth;
        String nextPrefix = currentPrefix + nextChildNode.label;
        if(extraDepth < neededExtraDepth){
            nextRow = new int[row.length + 1];
            System.arraycopy(row, 0, nextRow, 0, row.length);

            nextCol = new int[col.length];
            nextCol[0] = col[0] + 1;
            char colChar = nextChildNode.label;
            for (int i = 1; i < col.length; i++){
                if(colChar == queryContext.QueryString.GetCharacter(i - 1)){
                    nextCol[i] = col[i - 1];
                }
                else{
                    nextCol[i] = Math.min(Math.min(col[i - 1], col[i]), nextCol[i - 1]) + 1;
                }
            }

            nextEditDistance = nextCol[nextCol.length - 1];
            nextRow[nextRow.length - 1] = nextEditDistance;
            nextExtraDepth++;

        }
        else{
            nextCol = new int[col.length + 1];
            nextRow = new int[row.length + 1];
            nextEditDistance = calculateMinEditDistance(nextPrefix, nextCol, nextRow);
        }

        int minColEditDistance = nextCol[0];
        for(int i = 0; i < nextCol.length - 1; i++){
            int distance = nextCol[i];
            if(distance < minColEditDistance){
                minColEditDistance = distance;
            }
        }

        if(minColEditDistance < nextEditDistance){
            nextEditDistance = minColEditDistance;
            nextNeededExtraDepth++;
            nextQueryStringPosition = queryStringPosition;
        }

        if(nextEditDistance > queryContext.MaxEdits){
            return;
        }

        float nextEditDiscount = calculateEditDiscount(nextEditDistance);
        if(nextChildNode instanceof LeafTrieNode){
            addDeleteSpaceNode(nodeQueue, nextChildNode);
            ClusteringVector termClusterVector = ((LeafTrieNode)nextChildNode).getClusteringVector();
            ClusteringVector queryClusterVector = termClusterVector.pairwiseMultiply(clusteringDiscount);
            for(int i = 0; i < queryContext.getNumberOfClusters(); i++){
                TrieNode indexClusterRoot = queryContext.getIndexCluster(i);
                float discount = getTermCorrelationNormalizedDiscount(nextEditDiscount) * queryClusterVector.Vector[i];
                PrefixActiveNode nextChildActiveNode = new PrefixActiveNode(
                        queryContext,
                        indexClusterRoot,
                        getNextTermStack(nextChildNode),
                        nextPrefix,
                        nextQueryStringPosition,
                        queryClusterVector,
                        discount,
                        nextCol,
                        nextRow,
                        nextNeededExtraDepth,
                        nextExtraDepth);
                nodeQueue.add(nextChildActiveNode);
            }
        }
        else{
            PrefixActiveNode nextChildActiveNode = new PrefixActiveNode(
                    queryContext,
                    nextChildNode,
                    previousTerms,
                    nextPrefix,
                    nextQueryStringPosition,
                    clusteringDiscount,
                    nextEditDiscount,
                    nextCol,
                    nextRow,
                    nextNeededExtraDepth,
                    nextExtraDepth);
            nodeQueue.add(nextChildActiveNode);
        }
    }

    private int calculateMinEditDistance(String nextPrefix, int[] nextCol, int[] nextRow) {
        nextRow[0] = row[0] + 1;
        nextCol[0] = col[0] + 1;

        char rowChar = queryContext.QueryString.GetCharacter(queryStringPosition);
        processDistanceArray(rowChar, nextPrefix, row, nextRow);

        char colChar = nextPrefix.charAt(nextPrefix.length() - 1);
        processDistanceArray(colChar, queryContext.QueryString.getString(), col, nextCol);

        int lastEditDistance;
        if(rowChar == colChar){
            lastEditDistance = col[col.length - 1];
        }
        else{
            lastEditDistance = Math.min(
                    Math.min(nextRow[nextRow.length - 2], nextCol[nextCol.length - 2]),
                    col[col.length - 1]) + 1;
        }

        nextRow[nextRow.length - 1] = lastEditDistance;
        nextCol[nextCol.length - 1] = lastEditDistance;

        int minEditDistance = nextRow[0];
        for(int i = 1; i < nextRow.length; i++){
            int editDistanceAtPrefix = nextRow[i];
            if(editDistanceAtPrefix < minEditDistance){
                minEditDistance = editDistanceAtPrefix;
            }
        }

        return minEditDistance;
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

    private float calculateEditDiscount(int nextMinEditDistance){
        float discount = editDiscount;
        for(int i = 0; i < nextMinEditDistance - row[row.length - 1]; i++){
            discount *= queryContext.EditDiscount;
        }

        return discount;
    }

    private void addDeleteSpaceNode(final PriorityQueue<AbstractPrefixActiveNode> nodeQueue, TrieNode leafNode) {
        DeleteSpaceActiveNode deleteSpaceNode = new DeleteSpaceActiveNode(
                queryContext,
                leafNode,
                previousTerms,
                currentPrefix,
                editDiscount,
                row);
        nodeQueue.add(deleteSpaceNode);
    }

    private TrieNode[] getNextTermStack(TrieNode termNode) {
        TrieNode[] termStack;
        termStack = new TrieNode[previousTerms.length + 1];
        System.arraycopy(previousTerms, 0, termStack, 0, previousTerms.length);
        termStack[termStack.length - 1] = termNode;
        return termStack;
    }

    private float getTermCorrelationNormalizedDiscount(float nextEditDiscount) {
        return nextEditDiscount * queryPosition.getRank() / queryContext.getMaxRank();
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
        return currentPrefix.length() == neededExtraDepth + queryContext.QueryString.GetLength() && neededExtraDepth == extraDepth;
    }
}
