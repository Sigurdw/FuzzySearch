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
    public static SuggestionSet makeDefaultSuggestionSet(int numberOfTerms){
        SuggestionSet suggestionSet = new SuggestionSet(numberOfTerms);
        for(int i = 0; i < numberOfTerms; i++){
            suggestionSet.addTerm(null, i);
        }

        return suggestionSet;
    }

    private float rankEstimate;

    private final ISuggestionWrapper[] suggestionSet;

    public SuggestionSet(int numberOfTerms){
        suggestionSet = new ISuggestionWrapper[numberOfTerms];
    }

    public void addTerm(ISuggestionWrapper term, int index){
        suggestionSet[index] = term;
    }

    public float getRankEstimate(){
        return rankEstimate;
    }

    public SuggestionSet getNextSuggestionSet(IndexTraverser[] suggestionSources){
        SuggestionSet nextSuggestionSet = new SuggestionSet(suggestionSet.length);
        float minDifference = suggestionSet[0].getRank() - suggestionSources[0].peekNextAvailableSuggestionRank();
        int minIndex = 0;
        for(int i = 1; i < suggestionSet.length; i++){
            if(suggestionSet[i].getRank() - suggestionSources[i].peekNextAvailableSuggestionRank() < minDifference){
                nextSuggestionSet.addTerm(suggestionSet[minIndex], minIndex);
                minIndex = i;
            }
            else{
                nextSuggestionSet.addTerm(suggestionSet[i], i);
            }
        }

        ISuggestionWrapper nextSuggestionWrapper = suggestionSources[minIndex].getNextAvailableSuggestion();
        nextSuggestionSet.addTerm(nextSuggestionWrapper, minIndex);
        return nextSuggestionSet;
    }

    public float peekNextSetRank(IndexTraverser[] suggestionSources) {
        float minDifference = suggestionSet[0].getRank() - suggestionSources[0].peekNextAvailableSuggestionRank();
        float rankEstimate = 0;
        int minIndex = 0;
        for(int i = 1; i < suggestionSet.length; i++){
            if(suggestionSet[i].getRank() - suggestionSources[i].peekNextAvailableSuggestionRank() < minDifference){
                rankEstimate += suggestionSet[minIndex].getRank();
                minIndex = i;
            }
            else{
                rankEstimate += suggestionSet[i].getRank();
            }
        }

        rankEstimate += suggestionSources[minIndex].peekNextAvailableSuggestionRank();
        return rankEstimate / suggestionSources.length;
    }

    public float peekNextNodeRank(IndexTraverser[] suggestionSources){
        float minDifference = suggestionSet[0].getRank() - suggestionSources[0].peekNextNodeRank();
        float rankEstimate = 0;
        int minIndex = 0;
        for(int i = 1; i < suggestionSet.length; i++){
            if(suggestionSet[i].getRank() - suggestionSources[i].peekNextNodeRank() < minDifference){
                rankEstimate += suggestionSet[minIndex].getRank();
                minIndex = i;
            }
            else{
                rankEstimate += suggestionSet[i].getRank();
            }
        }

        rankEstimate += suggestionSources[minIndex].peekNextNodeRank();
        return rankEstimate / suggestionSources.length;
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