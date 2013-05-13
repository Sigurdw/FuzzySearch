package Query;

/**
 * User: Sigurd Wien
 * Date: 24.04.13
 * Time: 21:04
 */
public interface IndexTraverser {
    /**
     * Initiates the next character iteration.
     * @param queryString The updated query string.
     */
    public void updateQueryString(String queryString);

    /**
     * Peeks the rank of the next node.
     * @return The rank of the next node or -1 if no node is available.
     */
    public float peekNextNodeRank();

    /**
     * Explores the next active node.
     */
    public void exploreNextNode();

    /**
     * Peeks the rank of the next available suggestion.
     * @return The rank of the next suggestion or -2 if no suggestion is available.
     */
    public float peekNextAvailableSuggestionRank();

    /**
     * Gets the next available suggestion.
     * @return The next available suggestion.
     */
    public ISuggestionWrapper getNextAvailableSuggestion();
}