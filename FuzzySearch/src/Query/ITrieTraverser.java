package Query;

import java.util.ArrayList;

public interface ITrieTraverser {
    public ArrayList<SuggestionWrapper> addCharacter();

    public int getNumberOfNodesInLastIteration();

    public int getTotalNodes();
}
