package Query.SimpleInteractiveSearch;

import Config.SearchConfig;
import Query.ISuggestionWrapper;
import Query.IndexTraverser;
import Query.PriorityInteractiveSearch.SuggestionTraverser;
import Query.QueryContext;

import java.util.ArrayList;
import java.util.Collections;

public class SimpleIndexTraverser implements IndexTraverser {
    private final QueryContext queryContext;
    private final ArrayList<ISuggestionWrapper> suggestions = new ArrayList<ISuggestionWrapper>();

    private ArrayList<ActiveNode> activeNodes = new ArrayList<ActiveNode>();
    private int suggestionPointer = 0;

    public SimpleIndexTraverser(SearchConfig searchConfig){
        queryContext = new QueryContext(searchConfig);
        for(int i = 0; i < queryContext.getNumberOfClusters(); i++){
            activeNodes.add(new ActiveNode(queryContext, i));
        }
    }

    @Override
    public void updateQueryString(String queryString) {
        queryContext.QueryString.SetQueryString(queryString);
        suggestions.clear();
        suggestionPointer = 0;
    }

    @Override
    public float peekNextNodeRank() {
        if(activeNodes.size() > 0){
            return 0;
        }

        return -1;
    }

    @Override
    public void exploreNextNode() {
        exploreAllNodes();
        extractSuggestions();
    }

    private void exploreAllNodes() {
        ArrayList<ActiveNode> nextActiveNodes = new ArrayList<ActiveNode>();
        for(ActiveNode activeNode : activeNodes){
            activeNode.addCharacter(nextActiveNodes);
        }

        activeNodes = nextActiveNodes;
    }

    private void extractSuggestions() {
        for(ActiveNode activeNode : activeNodes){
            SuggestionTraverser suggestionTraverser = activeNode.getSuggestionTraverser();
            while (suggestionTraverser.getNextRank() != -1){
                suggestions.add(suggestionTraverser.getNextSuggestion());
            }
        }

        Collections.sort(suggestions);
    }

    @Override
    public float peekNextAvailableSuggestionRank() {
        if(suggestions.size() == suggestionPointer){
            return -2;
        }

        return suggestions.get(suggestionPointer).getRank();
    }

    @Override
    public ISuggestionWrapper getNextAvailableSuggestion() {
        ISuggestionWrapper suggestion =  suggestions.get(suggestionPointer);
        suggestionPointer++;
        return suggestion;
    }
}
