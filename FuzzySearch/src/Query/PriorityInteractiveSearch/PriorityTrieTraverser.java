package Query.PriorityInteractiveSearch;

import Config.SearchConfig;
import Query.ISuggestionWrapper;
import Query.IndexTraverser;
import Query.PriorityInteractiveSearch.Links.Link;
import Query.QueryContext;

import java.util.ArrayList;
import java.util.PriorityQueue;

public final class PriorityTrieTraverser implements IndexTraverser {
    private final QueryContext queryContext;
    private final PriorityQueue<Link> linkQueue = new PriorityQueue<Link>();
    private final PriorityQueue<SuggestionTraverser> suggestionQueue
            = new PriorityQueue<SuggestionTraverser>();
    private final ArrayList<PriorityActiveNode> exhaustedNodes = new ArrayList<PriorityActiveNode>();

    public PriorityTrieTraverser(SearchConfig searchConfig){
        this.queryContext = new QueryContext(searchConfig);
        for(int i = 0; i < queryContext.getNumberOfClusters(); i++){
            exhaustedNodes.add(new PriorityActiveNode(queryContext, i));
        }
    }

    @Override
    public void updateQueryString(String queryString) {
        queryContext.QueryString.SetQueryString(queryString);
        initiateFromExhaustedNodes();
    }

    private void initiateFromExhaustedNodes() {
        for(PriorityActiveNode exhaustedNode : exhaustedNodes){
            exhaustedNode.extractLinks(linkQueue);
        }

        exhaustedNodes.clear();
        suggestionQueue.clear();
        queryContext.SuggestionNodeRegister.clearRegister();
    }

    @Override
    public float peekNextNodeRank() {
        Link thresholdLink = linkQueue.peek();
        if(thresholdLink != null){
            return thresholdLink.getRank();
        }

        return -2;
    }

    @Override
    public void exploreNextNode() {
        Link nextLink = linkQueue.poll();
        PriorityActiveNode currentNode = nextLink.useLink(linkQueue);
        if(currentNode.isExhausted()){
            SuggestionTraverser suggestionTraverser = currentNode.getSuggestionsTraverser();
            suggestionQueue.add(suggestionTraverser);
            exhaustedNodes.add(currentNode);
        }
        else{
            currentNode.extractLinks(linkQueue);
        }
    }

    @Override
    public float peekNextAvailableSuggestionRank() {
        SuggestionTraverser nextSuggestionTraverser = suggestionQueue.peek();
        if(nextSuggestionTraverser != null){
            return nextSuggestionTraverser.getNextRank();
        }

        return -2;
    }

    @Override
    public ISuggestionWrapper getNextAvailableSuggestion() {
        SuggestionTraverser suggestionTraverser = suggestionQueue.poll();
        ISuggestionWrapper suggestion = suggestionTraverser.getNextSuggestion();
        if (suggestionTraverser.getNextRank() >= 0){
            suggestionQueue.add(suggestionTraverser);
        }

        return suggestion;
    }




}
