package Config;

import DataStructure.Index;

public class SearchConfig {
    private final Index currentIndex;
    private final int neededSuggestion;
    private final int allowedEdits;
    private final float editDiscount;
    private final boolean semanticEnabled;

    private final boolean separateTermEvaluation;
    private final boolean multiTerm;

    public static SearchConfig DummyConfig = new SearchConfig(null, 5, 1, 0.5f, false, false, true);

    private SearchConfig(Index currentIndex, int neededSuggestion, int allowedEdits, float editDiscount, boolean semanticEnabled, boolean separateTermEvaluation, boolean multiTerm){
        this.currentIndex = currentIndex;
        this.neededSuggestion = neededSuggestion;
        this.allowedEdits = allowedEdits;
        this.editDiscount = editDiscount;
        this.semanticEnabled = semanticEnabled;
        this.separateTermEvaluation = separateTermEvaluation;
        this.multiTerm = multiTerm;
    }

    public Index getCurrentIndex() {
        return currentIndex;
    }

    public int getAllowedEdits() {
        return allowedEdits;
    }

    public float getEditDiscount() {
        return editDiscount;
    }

    public int getNeededSuggestion() {
        return neededSuggestion;
    }

    public boolean isSemanticEnabled() {
        return semanticEnabled;
    }

    public boolean isSeparateTermEvaluation() {
        return separateTermEvaluation;
    }

    public boolean isMultiTerm(){
        return multiTerm;
    }

    public SearchConfig updateConfig(Index newIndex){
        return new SearchConfig(newIndex, neededSuggestion, allowedEdits, editDiscount, semanticEnabled, separateTermEvaluation, multiTerm);
    }

    public SearchConfig updateNeededSuggestionConfig(int neededSuggestion){
        return new SearchConfig(currentIndex, neededSuggestion, allowedEdits, editDiscount, semanticEnabled, separateTermEvaluation, multiTerm);
    }

    public SearchConfig updateConfig(int newAllowedEdits){
        return new SearchConfig(currentIndex, neededSuggestion, newAllowedEdits, editDiscount, semanticEnabled, separateTermEvaluation, multiTerm);
    }

    public SearchConfig updateConfig(float newEditDiscount){
        return new SearchConfig(currentIndex, neededSuggestion, allowedEdits, newEditDiscount, semanticEnabled, separateTermEvaluation, multiTerm);
    }

    public SearchConfig updateConfig(boolean newSemanticEnabled){
        return new SearchConfig(currentIndex, neededSuggestion, allowedEdits, editDiscount, newSemanticEnabled, separateTermEvaluation, multiTerm);
    }

    public SearchConfig updateSeparateTermEvaluation(boolean separateTermEvaluation){
        return new SearchConfig(currentIndex, neededSuggestion, allowedEdits, editDiscount, semanticEnabled, separateTermEvaluation, multiTerm);
    }

    public SearchConfig updateMultiTerm(boolean multiTerm){
        return new SearchConfig(currentIndex, neededSuggestion, allowedEdits, editDiscount, semanticEnabled, separateTermEvaluation, multiTerm);
    }

    @Override
    public String toString(){
        return "Index: " + currentIndex
                + " AllowedEdits :" + allowedEdits
                + " EditDiscount: " + editDiscount
                + " SemanticEnabled : " + semanticEnabled;
    }
}
