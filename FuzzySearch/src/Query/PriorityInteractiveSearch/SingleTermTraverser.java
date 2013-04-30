package Query.PriorityInteractiveSearch;

import Query.PriorityInteractiveSearch.Links.Link;
import Query.QueryContext;
import Query.SuggestionWrapper;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class SingleTermTraverser {
    private final QueryContext queryContext;
    private final PriorityQueue<Link> linkQueue = new PriorityQueue<Link>();
    private final PriorityQueue<SuggestionTraverser> suggestionQueue
            = new PriorityQueue<SuggestionTraverser>();
    private final ArrayList<PriorityActiveNode> exhaustedNodes = new ArrayList<PriorityActiveNode>();

    public SingleTermTraverser(QueryContext queryContext){
        this.queryContext = queryContext;
        for(int i = 0; i < queryContext.getNumberOfClusters(); i++){
            exhaustedNodes.add(new PriorityActiveNode(queryContext, i));
        }
    }
}
