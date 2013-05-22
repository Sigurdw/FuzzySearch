package Query.PrefixBasedInteractiveSearch;

import DataStructure.TrieNode;
import Query.QueryContext;
import Query.SuggestionTraversers.PrefixSuggestionTraverser;

import java.util.PriorityQueue;

/**
 * User: Sigurd Wien
 * Date: 15.05.13
 * Time: 22:58
 */
public final class DeleteSpaceActiveNode extends AbstractPrefixActiveNode{
    private final QueryContext queryContext;
    private final TrieNode leafNode;
    private final TrieNode[] previousTerms;
    private final String matchedPrefix;
    private int currentRowPosition;
    private int[] row;
    private float editDiscount;
    private int currentEditDistance;

    public DeleteSpaceActiveNode(
            QueryContext queryContext,
            TrieNode leafNode,
            TrieNode[] previousTerms,
            String matchedString,
            float editDiscount,
            int[] row) {
        this.queryContext = queryContext;
        this.leafNode = leafNode;
        this.previousTerms = previousTerms;
        this.matchedPrefix = matchedString;
        currentRowPosition = matchedString.length();
        this.editDiscount = editDiscount;
        this.row = row;
        currentEditDistance = findCurrentEditDistance();
    }

    private void updateRow(){
        int numberOfIterations = queryContext.QueryString.GetLength() - currentRowPosition;
        int editDiscountAtStart = currentEditDistance;
        for(int i = 0; i < numberOfIterations; i++){
            final char queryChar = queryContext.QueryString.GetCharacter(currentRowPosition + i);
            int[] nextRow = new int[row.length];
            nextRow[0] = currentRowPosition + i + 1;
            for(int j = 1; j < nextRow.length; j++){
                char prefixCharacter = matchedPrefix.charAt(j - 1);
                if(queryChar == prefixCharacter){
                    nextRow[j] = row[j - 1];
                }
                else{
                    nextRow[j] = Math.min(Math.min(row[j - 1], row[j]), nextRow[j - 1]) + 1;
                }
            }

            row = nextRow;
        }

        currentEditDistance = findCurrentEditDistance();
        int addedEditDistance = currentEditDistance - editDiscountAtStart;
        updateEditDiscount(addedEditDistance);
    }

    private int findCurrentEditDistance(){
        int minEditDistance = row[0];
        for(int i = 1; i < row.length; i++){
            int editDistanceAtPrefix = row[i];
            if(editDistanceAtPrefix < minEditDistance){
                minEditDistance = editDistanceAtPrefix;
            }
        }

        return minEditDistance;
    }

    private void updateEditDiscount(int addedEditDistance){
        editDiscount =  editDiscount * (float)Math.pow(queryContext.EditDiscount, addedEditDistance);
    }

    @Override
    public boolean hasMoreChildren() {
        updateRow();
        return currentEditDistance <= queryContext.MaxEdits;
    }

    @Override
    public void getNextChildNodes(PriorityQueue<AbstractPrefixActiveNode> nodeQueue) {

    }

    @Override
    public float getRank() {
        return leafNode.getRank() * editDiscount;
    }

    @Override
    public boolean isExhausted() {
        updateRow();
        return currentEditDistance <= queryContext.MaxEdits;
    }

    @Override
    public PrefixSuggestionTraverser getSuggestionTraverser() {
        return new PrefixSuggestionTraverser(leafNode, previousTerms, editDiscount);
    }
}
