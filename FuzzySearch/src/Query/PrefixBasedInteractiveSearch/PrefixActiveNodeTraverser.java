package Query.PrefixBasedInteractiveSearch;

import Config.SearchConfig;
import Query.ISuggestionWrapper;
import Query.IndexTraverser;
import Query.QueryContext;
import Query.SuggestionTraversers.PrefixSuggestionTraverser;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * User: Sigurd Wien
 * Date: 11.05.13
 * Time: 14:49
 */
public class PrefixActiveNodeTraverser implements IndexTraverser {
    private final QueryContext queryContext;
    private final PriorityQueue<AbstractPrefixActiveNode> nodeQueue = new PriorityQueue<AbstractPrefixActiveNode>();
    private final PriorityQueue<PrefixSuggestionTraverser> suggestionQueue
            = new PriorityQueue<PrefixSuggestionTraverser>();
    private final ArrayList<AbstractPrefixActiveNode> exhaustedNodes = new ArrayList<AbstractPrefixActiveNode>();

    public PrefixActiveNodeTraverser(SearchConfig searchConfig){
        queryContext = new QueryContext(searchConfig);
        for(int i = 0; i < queryContext.getNumberOfClusters(); i++){
            exhaustedNodes.add(new PrefixActiveNode(queryContext, i));
        }
    }

    @Override
    public void updateQueryString(String queryString) {
        queryContext.QueryString.SetQueryString(queryString);
        initiateFromExhaustedNodes();
    }

    private void initiateFromExhaustedNodes() {
        for(AbstractPrefixActiveNode exhaustedNode : exhaustedNodes){
            if(exhaustedNode.hasMoreChildren()){
                nodeQueue.add(exhaustedNode);
            }
        }

        exhaustedNodes.clear();
        suggestionQueue.clear();
    }

    @Override
    public float peekNextNodeRank() {
        AbstractPrefixActiveNode bestActiveNode = nodeQueue.peek();
        if(bestActiveNode != null){
            return bestActiveNode.getRank();
        }

        return -1;
    }

    @Override
    public void exploreNextNode() {
        AbstractPrefixActiveNode currentNode = nodeQueue.poll();
        if(currentNode.isExhausted()){
            PrefixSuggestionTraverser suggestionTraverser = currentNode.getSuggestionTraverser();
            suggestionQueue.add(suggestionTraverser);
            exhaustedNodes.add(currentNode);
        }
        else{
            currentNode.getNextChildNodes(nodeQueue);
            if(currentNode.hasMoreChildren()){
                nodeQueue.add(currentNode);
            }
        }
    }

    @Override
    public float peekNextAvailableSuggestionRank() {
        PrefixSuggestionTraverser bestTraverser = suggestionQueue.peek();
        if(bestTraverser != null){
            return bestTraverser.getNextRank();
        }

        return -2;
    }

    @Override
    public ISuggestionWrapper getNextAvailableSuggestion() {
        PrefixSuggestionTraverser suggestionTraverser = suggestionQueue.poll();
        ISuggestionWrapper suggestion = suggestionTraverser.getNextSuggestion(peekNextNodeRank());
        if(suggestionTraverser.getNextRank() >= 0){
            suggestionQueue.add(suggestionTraverser);
        }

        return suggestion;
    }
}
