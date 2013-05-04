package Query.PriorityInteractiveSearch;

import DataStructure.IQueryStringListener;
import Query.IndexTraverser;
import Query.QueryContext;
import Query.SuggestionWrapper;

import java.util.PriorityQueue;

/**
 * User: Sigurd Wien
 * Date: 24.04.13
 * Time: 21:29
 */

public class TermSeparatedIndexTraverser implements IndexTraverser {
    public final char separator = ' ';
    private final IndexTraverser[] termTraversers;
    private final QueryContext queryContext;
    private final PriorityQueue<SuggestionSet> suggestionSets = new PriorityQueue<SuggestionSet>();

    private SuggestionSet lastProducedSuggestionSet = null;

    public TermSeparatedIndexTraverser(QueryContext queryContext){
        this.queryContext = queryContext;
        termTraversers = new PriorityTrieTraverser[1];
        termTraversers[0] = new PriorityTrieTraverser(queryContext);
    }

    @Override
    public boolean isQueryExhausted() {
        return getCurrentTraverser().isQueryExhausted();
    }

    @Override
    public void initiateFromExhaustedNodes() {
        if(queryContext.QueryString.GetLastCharacter() == separator){
            addNewTerm();
        }

        for(IndexTraverser indexTraverser : termTraversers){
            indexTraverser.initiateFromExhaustedNodes();
        }


    }

    private void addNewTerm() {
        IndexTraverser[] newTraversers = new PriorityTrieTraverser[termTraversers.length + 1];
        for(int i = 0; i < termTraversers.length; i++){
            newTraversers[i] = termTraversers[i];
        }

        newTraversers[termTraversers.length] = new PriorityTrieTraverser(queryContext);
    }

    @Override
    public void exploreNextNode() {
        int traverserToExplore = getTraverserToExplore();
        termTraversers[traverserToExplore].exploreNextNode();
    }

    private int getTraverserToExplore(){
        int index = termTraversers.length - 1;
        for(int i = 0; i < termTraversers.length; i++){
            if(termTraversers[i].peekNextAvailableSuggestionRank() < 0){
                index = i;
                break;
            }
        }

        return index;
    }

    @Override
    public boolean hasAvailableSuggestions() {
        return suggestionSets.peek().getRankEstimate() >= lastProducedSuggestionSet.peekNextSetRank(termTraversers);
    }

    @Override
    public SuggestionWrapper getNextAvailableSuggestion() {
        //todo;
    }

    @Override
    public float peekNextAvailableSuggestionRank() {
        return lastProducedSuggestionSet.peekNextSetRank(termTraversers);
    }
    
    public float peekNextNodeRank(){
        return lastProducedSuggestionSet.peekNextNodeRank(termTraversers);
    }

    private IndexTraverser getCurrentTraverser(){
        return termTraversers[termTraversers.length - 1];
    }
}
