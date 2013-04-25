package Query.PriorityInteractiveSearch;

import Query.ISuggestionWrapper;

import java.util.ArrayList;

/**
 * User: Sigurd Wien
 * Date: 24.04.13
 * Time: 21:04
 */
public interface IndexTraverser {
    public boolean isQueryExhausted();

    public void initiateFromExhaustedNodes();

    public void exploreNextNode();

    public boolean hasAvailableSuggestions();

    public ArrayList<ISuggestionWrapper> getAvailableSuggestions(int numberOfSuggestion);
}