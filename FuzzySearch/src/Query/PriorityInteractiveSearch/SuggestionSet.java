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
            if (!suggestionSource.hasAvailableSuggestions()) {
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

    public SuggestionSet getNextSuggestionSet(IndexTraverser[] suggestionSources){
        SuggestionSet nextSuggestionSet = null;
        int minIndex = getMinDifferenceIndex(suggestionSources);
        if(suggestionSources[minIndex].hasAvailableSuggestions()){
            ISuggestionWrapper newSuggestionInSet = suggestionSources[minIndex].getNextAvailableSuggestion();
            if(newSuggestionInSet != null){
                nextSuggestionSet = new SuggestionSet(suggestionSet.length);
                System.arraycopy(suggestionSet, 0, nextSuggestionSet.suggestionSet, 0, suggestionSet.length);
                nextSuggestionSet.suggestionSet[minIndex] = newSuggestionInSet;
            }
        }

        return nextSuggestionSet;
    }

    public float peekNextSetRank(IndexTraverser[] suggestionSources) {
        float nextSetRank = -2;
        int minIndex = getMinDifferenceIndex(suggestionSources);
        if(suggestionSources[minIndex].hasAvailableSuggestions()){
            nextSetRank = 0;
            for(ISuggestionWrapper suggestionWrapper : suggestionSet){
                nextSetRank += suggestionWrapper.getRank();
            }

            nextSetRank +=
                    suggestionSources[minIndex].peekNextAvailableSuggestionRank() - suggestionSet[minIndex].getRank();
            nextSetRank /= suggestionSet.length;
        }

        return nextSetRank;
    }

    public float peekNextNodeRank(IndexTraverser[] suggestionSources){
        float minDifference = suggestionSet[0].getRank() - suggestionSources[0].peekNextNodeRank();
        float rankEstimate = 0;
        int minIndex = 0;
        for(int i = 1; i < suggestionSet.length; i++){
            float difference = suggestionSet[i].getRank() - suggestionSources[i].peekNextNodeRank();
            if(difference < minDifference){
                rankEstimate += suggestionSet[minIndex].getRank();
                minIndex = i;
                minDifference = difference;
            }
            else{
                rankEstimate += suggestionSet[i].getRank();
            }
        }

        rankEstimate += suggestionSources[minIndex].peekNextNodeRank();
        return rankEstimate / suggestionSources.length;
    }

    private int getMinDifferenceIndex(final IndexTraverser[] suggestionSources){
        float minDifference;
        int minIndex = 0;
        minDifference = getDifference(suggestionSet[0], suggestionSources[0]);

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
        float difference;
        if(suggestionSource.hasAvailableSuggestions()){
            difference =
                    suggestionWrapper.getRank() - suggestionSource.peekNextAvailableSuggestionRank();
        }
        else{
            difference = suggestionWrapper.getRank() - suggestionSource.peekNextNodeRank();
        }

        return difference;
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
}