package Query;

import DataStructure.TrieNode;

public class SuggestionWrapper implements Comparable<SuggestionWrapper>{
    private final TrieNode suggestionPosition;
    private final TrieNode[] previousTerms;
    private final float rankDiscount;

    public SuggestionWrapper(TrieNode suggestionPosition, TrieNode[] previousTerms, float rankDiscount){
        this.suggestionPosition = suggestionPosition;
        this.previousTerms = previousTerms;
        this.rankDiscount = rankDiscount;
    }

    public String getSuggestion() {
        StringBuilder stringBuilder = new StringBuilder();
        for(TrieNode term : previousTerms){
            stringBuilder.append(term.toString());
            stringBuilder.append(" ");
        }

        stringBuilder.append(suggestionPosition.toString());
        return stringBuilder.toString();
    }

    public float getRank() {
        return suggestionPosition.getRank() * rankDiscount;
    }

    public TrieNode getSuggestionPosition() {
        return suggestionPosition;
    }

    public float getRankDiscount() {
        return rankDiscount;
    }

    public int compareTo(SuggestionWrapper o) {
        double difference = o.getRank()- getRank();
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

    public String toString(){
        return getSuggestion() + ", " + getRank();
    }
}
