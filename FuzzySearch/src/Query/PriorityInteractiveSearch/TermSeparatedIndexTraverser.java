package Query.PriorityInteractiveSearch;

import Query.ISuggestionWrapper;
import Query.IndexTraverser;
import Query.QueryContext;

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
    public ISuggestionWrapper getNextAvailableSuggestion() {
        ISuggestionWrapper suggestion = null;
        while(peekNextAvailableSuggestionRank() > peekNextNodeRank()){
            if(lastProducedSuggestionSet.peekNextSetRank(termTraversers) > suggestionSets.peek().getRankEstimate()){
                lastProducedSuggestionSet = lastProducedSuggestionSet.getNextSuggestionSet(termTraversers);
                lastProducedSuggestionSet.calculateRankEstimate();
                suggestionSets.add(lastProducedSuggestionSet);
            }
            else{
                SuggestionSet bestSuggestion = suggestionSets.poll();
                suggestion = bestSuggestion.extractSuggestion();
            }
        }

        return suggestion;
    }

    @Override
    public float peekNextAvailableSuggestionRank() {
        float nextSetRank = lastProducedSuggestionSet.peekNextSetRank(termTraversers);
        float currentBestSetRank = suggestionSets.peek().getRankEstimate();

        return Math.max(nextSetRank, currentBestSetRank);
    }
    
    public float peekNextNodeRank(){
        return lastProducedSuggestionSet.peekNextNodeRank(termTraversers);
    }

    private IndexTraverser getCurrentTraverser(){
        return termTraversers[termTraversers.length - 1];
    }
}
