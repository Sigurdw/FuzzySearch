package DocumentModel;

import java.util.List;

public class MedlineDocument implements IDocument {

    private final String title;
    private final String path;

    public MedlineDocument(String title, String path){

        this.title = title;
        this.path = path;
    }

    @Override
    public String getName() {
        return title;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public List<String> getTokens() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
