package Query;

import DataStructure.QueryString;
import DataStructure.TrieNode;
import Query.PriorityInteractiveSearch.SuggestionNodeRegister;
import DataStructure.Index;

public final class QueryContext {
    public final Index Index;

    public final QueryString QueryString;

    public final Query.PriorityInteractiveSearch.SuggestionNodeRegister SuggestionNodeRegister;

    public final int MaxEdits;

    public final int NeededSuggestions;

    public QueryContext(Index index, int maxEdits, int neededSuggestions) {
        Index = index;
        QueryString = new QueryString();
        SuggestionNodeRegister = new SuggestionNodeRegister();
        MaxEdits = maxEdits;
        NeededSuggestions = neededSuggestions;
    }

    public float getMaxRank(){
        return Index.getMaxRank();
    }

    public TrieNode getIndexCluster(int clusterId){
        return Index.getIndexCluster(clusterId);
    }

    public int getNumberOfClusters(){
        return Index.indexHeader.numberOfClusters;
    }
}
