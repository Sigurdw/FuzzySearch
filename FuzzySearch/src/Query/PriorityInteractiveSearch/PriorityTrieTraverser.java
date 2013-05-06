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
    public void exploreNextNode() {
        Link nextLink = linkQueue.poll();
        if(nextLink != null){
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
    }

    @Override
    public boolean hasAvailableSuggestions() {
        return suggestionQueue.size() > 0;
    }

    @Override
    public ISuggestionWrapper getNextAvailableSuggestion() {
        ISuggestionWrapper suggestionWrapper = null;

        float thresholdRank = peekNextNodeRank();

        if (getNextSuggestionRank() >= thresholdRank){
            SuggestionTraverser suggestionTraverser = suggestionQueue.poll();
            suggestionWrapper = suggestionTraverser.getNextSuggestion(thresholdRank);
            suggestionQueue.add(suggestionTraverser);
        }

        return suggestionWrapper;
    }

    @Override
    public float peekNextNodeRank() {
        float thresholdRank = 0;
        Link thresholdLink = linkQueue.peek();
        if(thresholdLink != null){
            thresholdRank = thresholdLink.getRank();
        }

        return thresholdRank;
    }

    @Override
    public void updateQueryString(String queryString) {
        queryContext.QueryString.SetQueryString(queryString);
    }

    @Override
    public float peekNextAvailableSuggestionRank() {
        float thresholdRank = peekNextNodeRank();
        float nextSuggestionRank = getNextSuggestionRank();
        if(nextSuggestionRank >= thresholdRank){
            return nextSuggestionRank;
        }

        return -2;
    }

    private float getNextSuggestionRank() {
        float nextSuggestionRank = -3;
        SuggestionTraverser nextSuggestionTraverser = suggestionQueue.peek();
        if(nextSuggestionTraverser != null){
            nextSuggestionRank = nextSuggestionTraverser.getNextRank();
        }
        return nextSuggestionRank;
    }

    public void initiateFromExhaustedNodes() {
        for(PriorityActiveNode exhaustedNode : exhaustedNodes){
            exhaustedNode.extractLinks(linkQueue);
        }

        exhaustedNodes.clear();
        suggestionQueue.clear();
        queryContext.SuggestionNodeRegister.clearRegister();
    }

    public boolean isQueryExhausted(){
        return linkQueue.peek() == null;
    }
}
