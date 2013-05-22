package Query;

import Config.SearchConfig;
import DataStructure.QueryString;
import DataStructure.TrieNode;
import Query.SuggestionTraversers.SuggestionNodeRegister;
import DataStructure.Index;

public final class QueryContext {
    public final Index Index;

    public final QueryString QueryString;

    public final Query.SuggestionTraversers.SuggestionNodeRegister SuggestionNodeRegister;

    public final int MaxEdits;

    public final int NeededSuggestions;

    public final float EditDiscount;

    public final boolean MultiTerm;

    public QueryContext(SearchConfig searchConfig) {
        Index = searchConfig.getCurrentIndex();
        QueryString = new QueryString();
        SuggestionNodeRegister = new SuggestionNodeRegister();
        MaxEdits = searchConfig.getAllowedEdits();
        NeededSuggestions = searchConfig.getNeededSuggestion();
        EditDiscount = searchConfig.getEditDiscount();
        MultiTerm = searchConfig.isMultiTerm();
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
