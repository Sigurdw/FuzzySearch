package Query.PriorityInteractiveSearch;

import Config.SearchConfig;
import Query.ISuggestionWrapper;
import Query.IndexTraverser;

import java.util.PriorityQueue;

public class TermSeparatedIndexTraverser implements IndexTraverser {
    public final char separator = ' ';
    private final SearchConfig searchConfig;
    private final PriorityQueue<SuggestionSet> suggestionSets = new PriorityQueue<SuggestionSet>();

    private IndexTraverser[] termTraversers;
    private SuggestionSet lastProducedSuggestionSet = null;
    private int currentTermStart = 0;

    public TermSeparatedIndexTraverser(SearchConfig searchConfig){
        this.searchConfig = searchConfig;
        termTraversers = new PriorityTrieTraverser[1];
        termTraversers[0] = new PriorityTrieTraverser(searchConfig);
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

        for(IndexTraverser indexTraverser : termTraversers){
            indexTraverser.initiateFromExhaustedNodes();
        }

        suggestionSets.clear();
        lastProducedSuggestionSet = null;
    }

    private void addNewTerm() {
        IndexTraverser[] newTraversers = new PriorityTrieTraverser[termTraversers.length + 1];
        System.arraycopy(termTraversers, 0, newTraversers, 0, termTraversers.length);
        newTraversers[termTraversers.length] = new PriorityTrieTraverser(searchConfig);
        termTraversers = newTraversers;
    }

    @Override
    public float peekNextNodeRank(){
        return lastProducedSuggestionSet.peekNextNodeRank(termTraversers);
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

        while(peekNextAvailableSuggestionRank() > lastProducedSuggestionSet.getRankEstimate()){
            produceNextSuggestionSet();
        }

        float nextProducedSetRank = lastProducedSuggestionSet.peekNextSetRank(termTraversers);
        float currentBestSetRank = peekNextSuggestionRankInSuggestionQueue();
        return Math.max(nextProducedSetRank, currentBestSetRank);
    }

    private void produceNextSuggestionSet() {
        lastProducedSuggestionSet = lastProducedSuggestionSet.getNextSuggestionSet(termTraversers);
        lastProducedSuggestionSet.calculateRankEstimate();
        suggestionSets.add(lastProducedSuggestionSet);
    }

    private float peekNextSuggestionRankInSuggestionQueue(){
        float nextSuggestionRankInSuggestionQueue = -2;
        SuggestionSet suggestionSet = suggestionSets.peek();
        if(suggestionSet != null){
            nextSuggestionRankInSuggestionQueue = suggestionSets.peek().getRankEstimate();
        }

        return nextSuggestionRankInSuggestionQueue;
    }

    @Override
    public ISuggestionWrapper getNextAvailableSuggestion() {
        ISuggestionWrapper suggestion = null;
        if(lastProducedSuggestionSet.peekNextSetRank(termTraversers) > peekNextSuggestionRankInSuggestionQueue()){
            produceNextSuggestionSet();
        }
        else{
            SuggestionSet bestSuggestion = suggestionSets.poll();
            suggestion = bestSuggestion.extractSuggestion();
            break;
        }

        return suggestion;
    }

    private IndexTraverser getCurrentTraverser(){
        return termTraversers[termTraversers.length - 1];
    }
}
