package Query;

import java.util.ArrayList;

public interface ITrieTraverser {
    public ArrayList<ISuggestionWrapper> addCharacter();

    public int getNumberOfNodesInLastIteration();

    public int getTotalNodes();
}
