package Query.FastInteractiveSearch;

import DataStructure.QueryString;
import DataStructure.SuggestionCacheWrapper;
import DataStructure.Trie;
import DocumentModel.EditOperation;
import DocumentModel.IDocument;
import Query.*;
import Query.FastInteractiveSearch.Links.EditLink;
import Query.FastInteractiveSearch.Links.Link;
import Query.FastInteractiveSearch.Links.MatchLink;
import Query.FastInteractiveSearch.Links.SuggestionLink;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class FastActiveNode {
    private final Trie<IDocument> queryPosition;
    private final int previousEdits;
    private final int queryStringIndex;
    private final EditOperation lastEditOperation;
    private final QueryString queryString;
    private final double editDiscount;
    private final boolean isSubstitution;
    private final int maxEdits;

    private int nextChild = 0;
    private boolean firstAdded = false;
    private int nextSuggestion = 0;
    private boolean isNew = true;

    private FastActiveNode(
            Trie<IDocument> queryPosition,
            int previousEdits,
            EditOperation lastEditOperation,
            int queryStringIndex,
            QueryString queryString,
            double editDiscount,
            boolean isSubstitution,
            int maxEdits
    )
    {
        this.queryPosition = queryPosition;
        this.previousEdits = previousEdits;
        this.lastEditOperation = lastEditOperation;
        this.queryStringIndex = queryStringIndex;
        this.queryString = queryString;
        this.editDiscount = editDiscount;
        this.isSubstitution = isSubstitution;
        this.maxEdits = maxEdits;
    }

    public FastActiveNode(
            Trie<IDocument> rootNode,
            QueryString queryString,
            int maxEdits)
    {
        this(rootNode, 0, EditOperation.Match, 0, queryString, 1, false, maxEdits);
    }

    public void extractLinks(PriorityQueue<Link> linkQueue){
        if(!firstAdded){
            firstAdded = true;
            AddMatchToList(linkQueue);
            AddDeleteToList(linkQueue);
            AddNextEditsToList(linkQueue);
        }
    }

    private void AddMatchToList(PriorityQueue<Link> linkQueue){
        if(EditOperation.isOperationAllowed(lastEditOperation, EditOperation.Match)){
            Trie<IDocument> match = getMatch();
            if(match != null){
                double rank = getDiscountRank(match, EditOperation.getOperationDiscount(
                        EditOperation.Match,
                        previousEdits));
                int movement = EditOperation.getOperationMovement(EditOperation.Match);
                linkQueue.add(new MatchLink(
                        rank,
                        this,
                        match,
                        previousEdits,
                        queryStringIndex + movement,
                        editDiscount));
            }
        }
    }

    private void AddDeleteToList(PriorityQueue<Link> linkQueue){
        if(canDelete()){
            int cost = EditOperation.getOperationCost(lastEditOperation, EditOperation.Delete);
            if(previousEdits + cost <= maxEdits){
                double modifier = 1;
                if(cost != 0){
                    modifier = EditOperation.getOperationDiscount(
                            EditOperation.Delete,
                            previousEdits + cost);
                }

                int movement = EditOperation.getOperationMovement(EditOperation.Delete);

                double deleteRank = getDiscountRank(
                        queryPosition,
                        modifier);
                EditLink deleteLink = new EditLink(
                        deleteRank,
                        this,
                        queryPosition,
                        EditOperation.Delete,
                        previousEdits + cost,
                        queryStringIndex + movement,
                        editDiscount * modifier,
                        cost == 0);

                linkQueue.add(deleteLink);
            }
        }
    }

    private boolean canDelete(){
        return EditOperation.isOperationAllowed(lastEditOperation, EditOperation.Delete)
                && (!(getMatch() != null && queryPosition.getSize() == 1)
                || lastEditOperation == EditOperation.Insert);
    }

    private Trie<IDocument> getMatch(){
        return queryPosition.getChildren().get(getCharacter());
    }

    private void AddNextEditsToList(PriorityQueue<Link> linkQueue) {
        if(previousEdits + 1 <= maxEdits){
            EditLink insertLink = GetNextEditLink();
            if(insertLink != null){
                linkQueue.add(insertLink);
            }
        }
    }

    private EditLink GetNextEditLink(){
        if(EditOperation.isOperationAllowed(lastEditOperation, EditOperation.Insert) || isSubstitution){
            Trie<IDocument> bestEditNode = getNextEditNode();
            if(bestEditNode != null){
                double modifier = EditOperation.getOperationDiscount(EditOperation.Insert, previousEdits + 1);
                double rank = getDiscountRank(
                    bestEditNode,
                    modifier);

                int cost = EditOperation.getOperationCost(lastEditOperation, EditOperation.Insert);
                int movement = EditOperation.getOperationMovement(EditOperation.Insert);

                return new EditLink(
                    rank,
                    this,
                    bestEditNode,
                    EditOperation.Insert,
                    previousEdits + cost,
                    queryStringIndex + movement,
                    editDiscount * modifier,
                    false);
            }
        }

        return null;
    }

    private Trie<IDocument> getNextEditNode() {
        Trie<IDocument> bestEditNode = null;
        if(nextChild < queryPosition.getSize()){
            bestEditNode = queryPosition.getOrderedChild(nextChild);
            nextChild++;
            Trie<IDocument> match = queryPosition.getChildren().get(getCharacter());
            if(bestEditNode == match){
                bestEditNode = null;
                if(nextChild < queryPosition.getSize()){
                    bestEditNode = queryPosition.getOrderedChild(nextChild);
                    nextChild++;
                }
            }
        }

        return bestEditNode;
    }

    public void getSuggestions(
            ArrayList<ISuggestionWrapper> suggestionsWrappers,
            int neededSuggestions,
            double rankThreshold)
    {
        int numberOfUsedSuggestions = 0;
        ArrayList<SuggestionCacheWrapper<IDocument>> suggestions = queryPosition.getCachedSuggestions();
        for (
                int i = nextSuggestion;
                i < Math.min(nextSuggestion + neededSuggestions, suggestions.size());
                i++)
        {
            SuggestionCacheWrapper<IDocument> suggestionDocument = suggestions.get(i);

            double suggestionRank = getDiscountRank(suggestionDocument);
            if(rankThreshold > suggestionRank){
                break;
            }

            boolean hasSuggestion = false;
            for(ISuggestionWrapper suggestionWrapper : suggestionsWrappers){
                if(suggestionWrapper.getSuggestion().equals(suggestionDocument.getSuggestion().getLabel())){
                    hasSuggestion = true;
                    break;
                }
            }

            if(!hasSuggestion){
                suggestionsWrappers.add(new SuggestionWrapper(suggestionDocument.getSuggestion().getLabel(), suggestionRank));
                numberOfUsedSuggestions++;
            }
            else{
                nextSuggestion++;
            }
        }

        nextSuggestion += numberOfUsedSuggestions;
        //System.out.println("Got these: " + suggestionsWrappers);
    }

    public double getRank() {
        return getDiscountRank(queryPosition, 1);
    }

    private double getDiscountRank(Trie<IDocument> node, double modifier){
        return node.getRank() * modifier * editDiscount;
    }

    private double getDiscountRank(SuggestionCacheWrapper<IDocument> suggestion){
        return suggestion.getRank() * editDiscount;
    }

    public void maybyAddNextLink(EditOperation editOperation, PriorityQueue<Link> linkQueue){
        if(editOperation == EditOperation.Insert){
            AddNextEditsToList(linkQueue);
        }
    }

    public FastActiveNode createChild(
            Trie<IDocument> position,
            EditOperation editOperation,
            int numberOfEdits,
            int queryStringIndex,
            double editDiscount,
            boolean isSubstitution)
    {
        return new FastActiveNode(
                position,
                numberOfEdits,
                editOperation,
                queryStringIndex,
                queryString,
                editDiscount,
                isSubstitution,
                maxEdits
        );
    }

    public SuggestionLink getSuggestionLink(){
        double rank = getNextSuggestionRank();
        if(rank >= 0){
            return new SuggestionLink(
                    getNextSuggestionRank(),
                    queryString.GetLength(),
                    this);
        }

        return null;
    }

    private double getNextSuggestionRank() {
        if(nextSuggestion < queryPosition.getCachedSuggestions().size()){
            return getDiscountRank(queryPosition.getCachedSuggestions().get(nextSuggestion));
        }
        else{
            return -2;
        }
    }

    public String toString(){
        return "Query.ActiveQuery: " + queryPosition.getLabel() + " Last operation: " + lastEditOperation + " previous edits: " + previousEdits + " rank: " + getRank();
    }

    public String getLabel() {
        return queryPosition.getLabel();
    }

    private char getCharacter(){
        return queryString.GetCharacter(queryStringIndex);
    }

    public boolean isExhausted() {
        return queryString.IsExhausted(queryStringIndex);
    }

    public boolean isNew(){
        if(isNew){
            isNew = false;
            return true;
        }

        return false;
    }
}
