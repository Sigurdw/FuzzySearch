package DocumentModel;

import java.util.List;

public interface IDocument {
    public String getName();

    public String getPath();

    public List<String> getTokens();
}
