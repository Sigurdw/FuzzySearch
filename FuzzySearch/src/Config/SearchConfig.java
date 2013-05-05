package Config;

import DataStructure.Index;

/**
 * Created with IntelliJ IDEA.
 * User: Sigurd
 * Date: 30.03.13
 * Time: 15:05
 */
public class SearchConfig {
    private final Index currentIndex;
    private final int neededSuggestion;
    private final int allowedEdits;
    private final double editDiscount;
    private final boolean semanticEnabled;

    public static SearchConfig DummyConfig = new SearchConfig(null, 5, 1, 0.5, false);

    private SearchConfig(Index currentIndex, int neededSuggestion, int allowedEdits, double editDiscount, boolean semanticEnabled){
        this.currentIndex = currentIndex;
        this.neededSuggestion = neededSuggestion;
        this.allowedEdits = allowedEdits;
        this.editDiscount = editDiscount;
        this.semanticEnabled = semanticEnabled;
    }

    public Index getCurrentIndex() {
        return currentIndex;
    }

    public int getAllowedEdits() {
        return allowedEdits;
    }

    public double getEditDiscount() {
        return editDiscount;
    }

    public int getNeededSuggestion() {
        return neededSuggestion;
    }

    public boolean isSemanticEnabled() {
        return semanticEnabled;
    }

    public SearchConfig updateConfig(Index newIndex){
        return new SearchConfig(newIndex, neededSuggestion, allowedEdits, editDiscount, semanticEnabled);
    }

    public SearchConfig updateConfig(int newAllowedEdits){
        return new SearchConfig(currentIndex, neededSuggestion, newAllowedEdits, editDiscount, semanticEnabled);
    }

    public SearchConfig updateConfig(double newEditDiscount){
        return new SearchConfig(currentIndex, neededSuggestion, allowedEdits, newEditDiscount, semanticEnabled);
    }

    public SearchConfig updateConfig(boolean newSemanticEnabled){
        return new SearchConfig(currentIndex, neededSuggestion, allowedEdits, editDiscount, newSemanticEnabled);
    }

    @Override
    public String toString(){
        return "Index: " + currentIndex
                + " AllowedEdits :" + allowedEdits
                + " EditDiscount: " + editDiscount
                + " SemanticEnabled : " + semanticEnabled;
    }
}
