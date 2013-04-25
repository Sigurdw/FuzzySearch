package Query.PriorityInteractiveSearch;

import Query.ISuggestionWrapper;
import Query.QueryContext;
import java.util.ArrayList;

/**
 * User: Sigurd Wien
 * Date: 24.04.13
 * Time: 21:29
 */

public class TermSeparatedIndexTraverser implements IndexTraverser {
    public final char separator = ' ';
    public final ArrayList<PriorityTrieTraverser> traversers = new ArrayList<PriorityTrieTraverser>();

    public TermSeparatedIndexTraverser(QueryContext queryContext){
        traversers.add(new PriorityTrieTraverser(queryContext));
    }

    @Override
    public boolean isQueryExhausted() {
        return getCurrentTraverser().isQueryExhausted();
    }

    @Override
    public void initiateFromExhaustedNodes() {
        getCurrentTraverser().isQueryExhausted();
    }

    @Override
    public void exploreNextNode() {
        getCurrentTraverser().exploreNextNode();
    }

    @Override
    public ArrayList<ISuggestionWrapper> getAvailableSuggestions() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private PriorityTrieTraverser getCurrentTraverser(){
        return traversers.get(traversers.size() - 1);
    }
}
