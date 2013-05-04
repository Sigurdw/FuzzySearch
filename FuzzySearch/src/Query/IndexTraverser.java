package Query;

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

    public ISuggestionWrapper getNextAvailableSuggestion();

    public float peekNextAvailableSuggestionRank();

    public float peekNextNodeRank();
}