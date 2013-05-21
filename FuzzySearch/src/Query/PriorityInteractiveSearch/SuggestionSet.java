package Query.PriorityInteractiveSearch;

import Query.ISuggestionWrapper;
import Query.IndexTraverser;
import Query.RecursiveSuggestionWrapper;

/**
 * User: Sigurd Wien
 * Date: 02.05.13
 * Time: 11:48
 */
public final class SuggestionSet implements Comparable<SuggestionSet>
{
    private float rankEstimate;

    private final ISuggestionWrapper[] suggestionSet;

    public SuggestionSet(int numberOfTerms){
        suggestionSet = new ISuggestionWrapper[numberOfTerms];
    }

    public boolean initializeAsFirstSuggestionSet(IndexTraverser[] suggestionSources){
        for (IndexTraverser suggestionSource : suggestionSources) {
            if (suggestionSource.peekNextAvailableSuggestionRank() == -2) {
                return false;
            }
        }

        for(int i = 0; i < suggestionSources.length; i++){
            suggestionSet[i] = suggestionSources[i].getNextAvailableSuggestion();
        }

        calculateRankEstimate();
        return true;
    }

    public float getRankEstimate(){
        return rankEstimate;
    }

    public float peekNextSetRank(IndexTraverser[] suggestionSources) {
        int minIndex = getMinDifferenceIndex(suggestionSources);
        float nextSuggestionInSetRank = suggestionSources[minIndex].peekNextAvailableSuggestionRank();
        if(nextSuggestionInSetRank == -2){
            return -2;
        }

        float nextSetRank = 0;
        for(ISuggestionWrapper suggestionWrapper : suggestionSet){
            nextSetRank += suggestionWrapper.getRank();
        }

        nextSetRank += nextSuggestionInSetRank - suggestionSet[minIndex].getRank();
        nextSetRank /= suggestionSet.length;
        return nextSetRank;
    }

    public SuggestionSet getNextSuggestionSet(IndexTraverser[] suggestionSources){
        int minIndex = getMinDifferenceIndex(suggestionSources);
        ISuggestionWrapper newSuggestionInSet = suggestionSources[minIndex].getNextAvailableSuggestion();
        SuggestionSet nextSuggestionSet = new SuggestionSet(suggestionSet.length);
        System.arraycopy(suggestionSet, 0, nextSuggestionSet.suggestionSet, 0, suggestionSet.length);
        nextSuggestionSet.suggestionSet[minIndex] = newSuggestionInSet;
        return nextSuggestionSet;
    }

    private int getMinDifferenceIndex(final IndexTraverser[] suggestionSources){
        int minIndex = 0;
        float minDifference = getDifference(suggestionSet[0], suggestionSources[0]);

        for(int i = 1; i < suggestionSet.length; i++){
            float difference = getDifference(suggestionSet[i], suggestionSources[i]);
            if(difference < minDifference){
                minIndex = i;
                minDifference = difference;
            }
        }

        return minIndex;
    }

    private float getDifference(ISuggestionWrapper suggestionWrapper, IndexTraverser suggestionSource) {
        float availableSuggestionRank = suggestionSource.peekNextAvailableSuggestionRank();
        if(availableSuggestionRank == -2){
            return Float.MAX_VALUE;
        }

        return suggestionWrapper.getRank() - availableSuggestionRank;
    }

    public float peekNextNodeRank(IndexTraverser[] suggestionSources){
        int minIndex = getNextTrieTraverserIndex(suggestionSources);
        if(minIndex == -1){
            return -1;
        }

        float rank = 0;
        for (ISuggestionWrapper suggestion : suggestionSet){
            rank += suggestion.getRank();
        }

        rank -= suggestionSet[minIndex].getRank();
        rank += suggestionSources[minIndex].peekNextNodeRank();
        return rank / suggestionSources.length;
    }

    public void calculateRankEstimate(){
        rankEstimate = 0;
        for(ISuggestionWrapper suggestionWrapper : suggestionSet){
            rankEstimate += suggestionWrapper.getRank();
        }

        rankEstimate /= suggestionSet.length;
    }

    public ISuggestionWrapper extractSuggestion(){
        return new RecursiveSuggestionWrapper(suggestionSet, rankEstimate);
    }

    public int compareTo(SuggestionSet otherSuggestionSet){
        float difference = otherSuggestionSet.rankEstimate - this.rankEstimate;
        if(difference > 0){
            return 1;
        }
        else if(difference < 0){
            return -1;
        }
        else{
            return 0;
        }
    }

    public static float peekInitialNodeRank(IndexTraverser[] termTraversers) {
        float rank = 0;
        for (IndexTraverser indexTraverser : termTraversers){
            float nodeRank = indexTraverser.peekNextNodeRank();
            float suggestionRank = indexTraverser.peekNextAvailableSuggestionRank();
            float maxRank = Math.max(nodeRank, suggestionRank);
            if(maxRank == -1){
                return -1;
            }

            rank += maxRank;
        }

        return rank / termTraversers.length;
    }

    public static int getInitialTrieTraverserToExplore(IndexTraverser[] termTraverser){
        int minIndex = -1;
        float minDifference = Float.MAX_VALUE;
        for(int i = 0; i < termTraverser.length; i++){
            float nodeRank = termTraverser[i].peekNextNodeRank();
            if(nodeRank != -1){
                float suggestionRank = termTraverser[i].peekNextAvailableSuggestionRank();
                if(suggestionRank == -2){
                    return i;
                }

                float difference = suggestionRank - nodeRank;
                if(difference < minDifference){
                    minDifference = difference;
                    minIndex = i;
                }
            }
        }

        return minIndex;
    }

    public int getNextTrieTraverserIndex(IndexTraverser[] termTraversers) {
        float minDifference = Float.MAX_VALUE;
        int minIndex = -1;
        for(int i = 0; i < suggestionSet.length; i++){
            float nodeRank = termTraversers[i].peekNextNodeRank();
            float difference = Float.MAX_VALUE;
            if(nodeRank != -1){
                difference = suggestionSet[i].getRank() - nodeRank;
            }

            if(difference < minDifference){
                minIndex = i;
                minDifference = difference;
            }
        }

        return minIndex;
    }
}