package Query.PriorityInteractiveSearch;

import Query.ISuggestionWrapper;
import Query.IndexTraverser;

import java.util.ArrayList;

/**
 * User: Sigurd Wien
 * Date: 14.05.13
 * Time: 14:28
 */
public class SuggestionCacheIndexTraverser implements IndexTraverser {

    private final IndexTraverser indexTraverser;

    private int nextSuggestionInCache = 0;

    private ArrayList<ISuggestionWrapper> suggestionCache = new ArrayList<ISuggestionWrapper>();

    public SuggestionCacheIndexTraverser(IndexTraverser indexTraverser){
        this.indexTraverser = indexTraverser;
    }

    @Override
    public void updateQueryString(String queryString) {
        nextSuggestionInCache = 0;
        if(queryString != null){
            resetCache(queryString);
        }
    }

    private void resetCache(String queryString) {
        indexTraverser.updateQueryString(queryString);
        suggestionCache.clear();
    }

    @Override
    public float peekNextNodeRank() {
        return indexTraverser.peekNextNodeRank();
    }

    @Override
    public void exploreNextNode() {
        indexTraverser.exploreNextNode();
    }

    @Override
    public float peekNextAvailableSuggestionRank() {
        if(nextSuggestionInCache < suggestionCache.size()){
            return suggestionCache.get(nextSuggestionInCache).getRank();
        }
        else{
            return indexTraverser.peekNextAvailableSuggestionRank();
        }
    }

    @Override
    public ISuggestionWrapper getNextAvailableSuggestion() {
        ISuggestionWrapper suggestion;
        if(nextSuggestionInCache < suggestionCache.size()){
            suggestion = suggestionCache.get(nextSuggestionInCache);
        }
        else{
            suggestion = indexTraverser.getNextAvailableSuggestion();
            suggestionCache.add(suggestion);
        }

        nextSuggestionInCache++;
        return suggestion;
    }
}
