package Query.PrefixBasedInteractiveSearch;

import Query.SuggestionTraversers.PrefixSuggestionTraverser;

import java.util.PriorityQueue;

/**
 * User: Sigurd Wien
 * Date: 15.05.13
 * Time: 22:58
 */
public abstract class AbstractPrefixActiveNode implements Comparable<AbstractPrefixActiveNode> {
    public abstract boolean hasMoreChildren();

    public abstract void getNextChildNodes(final PriorityQueue<AbstractPrefixActiveNode> nodeQueue);

    public abstract float getRank();

    public abstract boolean isExhausted();

    public abstract PrefixSuggestionTraverser getSuggestionTraverser();

    @Override
    public int compareTo(AbstractPrefixActiveNode otherNode) {
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
