package Query.PrefixBasedInteractiveSearch;

import DataStructure.TrieNode;
import Query.QueryContext;

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
    private final int queryLengthAtCreation;
    private final float startDiscount;

    public DeleteSpaceActiveNode(
            QueryContext queryContext,
            TrieNode leafNode,
            TrieNode[] previousTerms,
            int queryLengthAtCreation,
            float startDiscount) {
        this.queryContext = queryContext;
        this.leafNode = leafNode;
        this.previousTerms = previousTerms;
        this.queryLengthAtCreation = queryLengthAtCreation;
        this.startDiscount = startDiscount;
    }

    @Override
    public boolean hasMoreChildren() {
        return false;
    }

    @Override
    public void getNextChildNodes(PriorityQueue<AbstractPrefixActiveNode> nodeQueue) {
        //Empty, should throw.
    }

    @Override
    public float getRank() {
        return leafNode.getRank() * getEditDiscount();
    }

    @Override
    public boolean isExhausted() {
        return true;
    }

    @Override
    public PrefixSuggestionTraverser getSuggestionTraverser() {
        return new PrefixSuggestionTraverser(leafNode, previousTerms, getEditDiscount());
    }

    private float getEditDiscount(){
        return startDiscount * (float)Math.pow(queryContext.EditDiscount, queryContext.QueryString.GetLength() - queryLengthAtCreation);
    }
}
