package Query;

import DataStructure.TrieNode;

public class SuggestionWrapper extends ISuggestionWrapper {
    private final TrieNode suggestionPosition;
    private final TrieNode[] previousTerms;
    private final float rankDiscount;

    public SuggestionWrapper(TrieNode suggestionPosition, TrieNode[] previousTerms, float rankDiscount){
        this.suggestionPosition = suggestionPosition;
        this.previousTerms = previousTerms;
        this.rankDiscount = rankDiscount;
    }

    @Override
    public String getSuggestion() {
        StringBuilder stringBuilder = new StringBuilder();
        for(TrieNode term : previousTerms){
            stringBuilder.append(term.toString());
            stringBuilder.append(" ");
        }

        stringBuilder.append(suggestionPosition.toString());
        return stringBuilder.toString();
    }

    @Override
    public float getRank() {
        return suggestionPosition.getRank() * rankDiscount;
    }
}
