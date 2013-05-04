package Query;

public class RecursiveSuggestionWrapper extends ISuggestionWrapper {

    private final ISuggestionWrapper[] suggestionSet;

    private final float rank;

    public RecursiveSuggestionWrapper(ISuggestionWrapper[] suggestionSet, float rank) {
        this.suggestionSet = suggestionSet;
        this.rank = rank;
    }

    @Override
    public String getSuggestion() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < suggestionSet.length - 1; i++){
            sb.append(suggestionSet[i].getSuggestion());
            sb.append(" ");
        }

        sb.append(suggestionSet[suggestionSet.length - 1]);
        return sb.toString();
    }

    @Override
    public float getRank() {
        return rank;
    }
}
