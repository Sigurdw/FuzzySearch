package Query.FastInteractiveSearch.Links;

import DataStructure.Trie;
import DocumentModel.IDocument;
import DocumentModel.EditOperation;
import Query.FastInteractiveSearch.FastActiveNode;

import java.util.PriorityQueue;

public class MatchLink extends Link{
    FastActiveNode sourceNode;
    Trie<IDocument> position;
    private int numberOfEdits;
    private int queryStringIndex;
    private double editDiscount;

    public MatchLink(
            double rank,
            FastActiveNode sourceNode,
            Trie<IDocument> position,
            int numberOfEdits,
            int queryStringIndex,
            double editDiscount) {
        super(rank);
        this.sourceNode = sourceNode;
        this.position = position;
        this.numberOfEdits = numberOfEdits;
        this.queryStringIndex = queryStringIndex;
        this.editDiscount = editDiscount;
    }

    @Override
    public FastActiveNode UseLink(PriorityQueue<Link> linkQueue) {
        return sourceNode.createChild(
                position,
                EditOperation.Match,
                numberOfEdits,
                queryStringIndex,
                editDiscount,
                false);
    }

    public String toString(){
        return "Match from " + sourceNode.getLabel() + " to " + position.getLabel() + " " + super.toString();
    }
}
