package Config;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Sigurd
 * Date: 30.03.13
 * Time: 15:05
 */
public class SearchConfig {
    private final File currentIndex;
    private final int allowedEdits;
    private final double editDiscount;
    private final boolean semanticEnabled;

    public static SearchConfig DummyConfig = new SearchConfig(null, 1, 0.5, false);

    private SearchConfig(File currentIndex, int allowedEdits, double editDiscount, boolean semanticEnabled){
        this.currentIndex = currentIndex;
        this.allowedEdits = allowedEdits;
        this.editDiscount = editDiscount;
        this.semanticEnabled = semanticEnabled;
    }

    public File getCurrentIndex() {
        return currentIndex;
    }

    public int getAllowedEdits() {
        return allowedEdits;
    }

    public double getEditDiscount() {
        return editDiscount;
    }

    public boolean needReIndexing(SearchConfig previousConfig){
        return currentIndex.getPath().equals(previousConfig.currentIndex.getPath());
    }

    public boolean isSemanticEnabled() {
        return semanticEnabled;
    }

    public SearchConfig updateConfig(File newIndex){
        return new SearchConfig(newIndex, allowedEdits, editDiscount, semanticEnabled);
    }

    public SearchConfig updateConfig(int newAllowedEdits){
        return new SearchConfig(currentIndex, newAllowedEdits, editDiscount, semanticEnabled);
    }

    public SearchConfig updateConfig(double newEditDiscount){
        return new SearchConfig(currentIndex, allowedEdits, newEditDiscount, semanticEnabled);
    }

    public SearchConfig updateConfig(boolean newSemanticEnabled){
        return new SearchConfig(currentIndex, allowedEdits, editDiscount, newSemanticEnabled);
    }

    @Override
    public String toString(){
        return "Index: " + currentIndex
                + " AllowedEdits :" + allowedEdits
                + " EditDiscount: " + editDiscount
                + " SemanticEnabled : " + semanticEnabled;
    }
}
