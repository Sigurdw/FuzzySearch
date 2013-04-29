package Query.PriorityInteractiveSearch;

import Query.SuggestionWrapper;

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

    public ArrayList<SuggestionWrapper> getAvailableSuggestions(int numberOfSuggestion);

    public int numberOfRetrievedSuggestions();
}