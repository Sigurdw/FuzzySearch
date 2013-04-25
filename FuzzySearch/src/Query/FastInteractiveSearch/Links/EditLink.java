package Query.FastInteractiveSearch.Links;

import DataStructure.Trie;
import DocumentModel.IDocument;
import DocumentModel.EditOperation;
import DataStructure.QueryString;
import Query.FastInteractiveSearch.FastActiveNode;

import java.util.PriorityQueue;

public class EditLink extends Link {
    Trie<IDocument> position;
    FastActiveNode sourceNode;
    EditOperation editOperation;
    private int numberOfEdits;
    private int queryStringIndex;
    private double editDiscount;
    private boolean substitution;

    public EditLink(
            double rank,
            FastActiveNode sourceNode,
            Trie<IDocument> position,
            EditOperation editOperation,
            int numberOfEdits,
            int queryStringIndex,
            double editDiscount,
            boolean isSubstitution)
    {
        super(rank);
        this.position = position;
        this.sourceNode = sourceNode;
        this.editOperation = editOperation;
        this.numberOfEdits = numberOfEdits;
        this.queryStringIndex = queryStringIndex;
        this.editDiscount = editDiscount;
        substitution = isSubstitution;
    }

    @Override
    public FastActiveNode UseLink(PriorityQueue<Link> linkQueue) {
        sourceNode.maybyAddNextLink(editOperation, linkQueue);
        return sourceNode.createChild(
                position,
                editOperation,
                numberOfEdits,
                queryStringIndex,
                editDiscount,
                substitution);
    }

    public String toString(){
        return editOperation +
                " link from " +
                sourceNode.getLabel() +
                " to " + position.getLabel() +
                " " + super.toString();
    }

    @Override
    public boolean isValid(QueryString queryString) {
        return true; //return (double)numberOfEdits / (double)queryString.GetLength() < 0.75;
    }
}
