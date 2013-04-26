package Query.PriorityInteractiveSearch;

import Query.ISuggestionWrapper;
import Query.PriorityInteractiveSearch.Links.Link;
import Query.QueryContext;

import java.util.ArrayList;
import java.util.PriorityQueue;

public final class PriorityTrieTraverser implements IndexTraverser{
    private final QueryContext queryContext;
    private final PriorityQueue<Link> linkQueue = new PriorityQueue<Link>();
    private final PriorityQueue<SuggestionTraverser> suggestionQueue
            = new PriorityQueue<SuggestionTraverser>();
    private final ArrayList<PriorityActiveNode> exhaustedNodes = new ArrayList<PriorityActiveNode>();
    private final ArrayList<ISuggestionWrapper> suggestions = new ArrayList<ISuggestionWrapper>();

    public PriorityTrieTraverser(QueryContext queryContext){
        this.queryContext = queryContext;
        for(int i = 0; i < queryContext.getNumberOfClusters(); i++){
            exhaustedNodes.add(new PriorityActiveNode(queryContext, i));
        }
    }

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
    public ArrayList<ISuggestionWrapper> getAvailableSuggestions(int numberOfSuggestionRequired) {
        float thresholdRank = 0;
        Link thresholdLink = linkQueue.peek();
        if(thresholdLink != null){
            thresholdRank = thresholdLink.getRank();
        }

        float nextSuggestionRank = getNextSuggestionRank();

        while (nextSuggestionRank >= thresholdRank && 0 < numberOfSuggestionRequired){
            SuggestionTraverser suggestionTraverser = suggestionQueue.poll();
            nextSuggestionRank = getNextSuggestionRank();

            int numberOfSuggestionsBefore = suggestions.size();
            suggestionTraverser.getSuggestions(
                    suggestions,
                    numberOfSuggestionRequired,
                    Math.max(thresholdRank, nextSuggestionRank));

            numberOfSuggestionRequired -= suggestions.size() - numberOfSuggestionsBefore;
            suggestionQueue.add(suggestionTraverser);
        }

        return suggestions;
    }

    @Override
    public int numberOfRetrievedSuggestions() {
        return suggestions.size();
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
        suggestions.clear();
        suggestionQueue.clear();
        queryContext.SuggestionNodeRegister.clearRegister();
    }

    public boolean isQueryExhausted(){
        return linkQueue.peek() == null;
    }
}
