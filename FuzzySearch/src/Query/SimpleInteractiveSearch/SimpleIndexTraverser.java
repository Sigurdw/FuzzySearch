package Query.SimpleInteractiveSearch;

import Config.SearchConfig;
import Query.ISuggestionWrapper;
import Query.IndexTraverser;
import Query.QueryContext;

import java.util.ArrayList;

public class SimpleIndexTraverser implements IndexTraverser {
    private final QueryContext queryContext;
    private ArrayList<ActiveNode> activeNodes = new ArrayList<ActiveNode>();


    public SimpleIndexTraverser(SearchConfig searchConfig){
        queryContext = new QueryContext(searchConfig);
        for(int i = 0; i < queryContext.getNumberOfClusters(); i++){
            activeNodes.add(new ActiveNode());
        }
    }

    @Override
    public void updateQueryString(String queryString) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public float peekNextNodeRank() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exploreNextNode() {
        ArrayList<ActiveNode> nextActiveNodes = new ArrayList<ActiveNode>();
        for(ActiveNode activeNode : activeNodes){
            activeNode.addCharacter(nextActiveNodes);
        }

        ArrayList<ISuggestionWrapper> suggestions = new ArrayList<ISuggestionWrapper>();
        for(ActiveNode activeNode : nextActiveNodes){
            activeNode.getSuggestions(suggestions);
        }

        activeNodes = nextActiveNodes;

    }

    @Override
    public float peekNextAvailableSuggestionRank() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ISuggestionWrapper getNextAvailableSuggestion() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
