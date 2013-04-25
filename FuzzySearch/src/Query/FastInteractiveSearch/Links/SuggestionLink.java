package Query.FastInteractiveSearch.Links;
import DataStructure.QueryString;
import Query.FastInteractiveSearch.FastActiveNode;

import java.util.PriorityQueue;

public class SuggestionLink extends Link{

    private final int validIterationId;
    private final FastActiveNode destinationNode;

    public SuggestionLink(double rank, int validIterationId, FastActiveNode destinationNode) {
        super(rank);
        this.validIterationId = validIterationId;
        this.destinationNode = destinationNode;
    }

    @Override
    public FastActiveNode UseLink(PriorityQueue<Link> linkQueue) {
        return destinationNode;
    }

    public boolean isValid(QueryString queryString){
        return queryString.GetLength() == validIterationId;
    }

    public String toString(){
        return "SuggestionLink to " + destinationNode.getLabel()
                + " " + super.toString();
    }
}
