package Query.TermSeparatedInteractiveSearch;

import Config.SearchConfig;
import Query.ISuggestionWrapper;
import Query.IndexTraverser;
import Query.PriorityInteractiveSearch.PriorityTrieTraverser;

import java.util.PriorityQueue;

public class TermSeparatedIndexTraverser implements IndexTraverser {
    public final char separator = ' ';
    private final SearchConfig searchConfig;
    private final PriorityQueue<SuggestionSet> suggestionSets = new PriorityQueue<SuggestionSet>();

    private IndexTraverser[] termTraversers;
    private SuggestionSet lastProducedSuggestionSet = null;
    private int currentTermStart = 0;

    public TermSeparatedIndexTraverser(SearchConfig searchConfig){
        this.searchConfig = searchConfig.updateMultiTerm(false);
        termTraversers = new IndexTraverser[0];
        addNewTerm();
    }

    @Override
    public void updateQueryString(String queryString) {
        if(queryString.charAt(queryString.length() - 1) == separator){
            addNewTerm();
            currentTermStart = queryString.length();
        }
        else{
            String currentTerm = queryString.substring(currentTermStart);
            getCurrentTraverser().updateQueryString(currentTerm);
        }

        for(int i = 0; i < termTraversers.length - 1; i++){
            termTraversers[i].updateQueryString(null);
        }

        suggestionSets.clear();
        lastProducedSuggestionSet = null;
    }

    private void addNewTerm() {
        IndexTraverser[] newTraversers = new IndexTraverser[termTraversers.length + 1];
        System.arraycopy(termTraversers, 0, newTraversers, 0, termTraversers.length);
        newTraversers[termTraversers.length] =
                new SuggestionCacheIndexTraverser(new PriorityTrieTraverser(searchConfig));
        termTraversers = newTraversers;
    }

    @Override
    public float peekNextNodeRank(){
        if(lastProducedSuggestionSet != null){
            return lastProducedSuggestionSet.peekNextNodeRank(termTraversers);
        }

        return SuggestionSet.peekInitialNodeRank(termTraversers);
    }

    @Override
    public void exploreNextNode() {
        int indexToExplore;
        if(lastProducedSuggestionSet != null){
            indexToExplore = lastProducedSuggestionSet.getNextTrieTraverserIndex(termTraversers);
        }
        else {
            indexToExplore = SuggestionSet.getInitialTrieTraverserToExplore(termTraversers);
        }

        if(indexToExplore != -1){
            termTraversers[indexToExplore].exploreNextNode();
        }
    }

    private int getTraverserToExplore(){
        int index = termTraversers.length - 1;
        for(int i = 0; i < termTraversers.length; i++){
            if(termTraversers[i].peekNextAvailableSuggestionRank() == -2){
                index = i;
            }
        }

        return index;
    }

    @Override
    public float peekNextAvailableSuggestionRank() {
        if(lastProducedSuggestionSet == null){
            SuggestionSet firstSuggestionSet = new SuggestionSet(termTraversers.length);
            if(firstSuggestionSet.initializeAsFirstSuggestionSet(termTraversers)){
                lastProducedSuggestionSet = firstSuggestionSet;
                suggestionSets.add(firstSuggestionSet);
            }
            else{
                return -2;
            }
        }

        while(lastProducedSuggestionSet.peekNextSetRank(termTraversers) > peekNextProducedSuggestionRank()){
            produceNextSuggestionSet();
        }

        return peekNextProducedSuggestionRank();
    }

    private void produceNextSuggestionSet() {
        lastProducedSuggestionSet = lastProducedSuggestionSet.getNextSuggestionSet(termTraversers);
        lastProducedSuggestionSet.calculateRankEstimate();
        suggestionSets.add(lastProducedSuggestionSet);
    }

    private float peekNextProducedSuggestionRank(){
        SuggestionSet suggestionSet = suggestionSets.peek();
        if(suggestionSet != null){
             return suggestionSet.getRankEstimate();
        }

        return -2;
    }

    @Override
    public ISuggestionWrapper getNextAvailableSuggestion() {
        return suggestionSets.poll().extractSuggestion();
    }

    private IndexTraverser getCurrentTraverser(){
        return termTraversers[termTraversers.length - 1];
    }
}
