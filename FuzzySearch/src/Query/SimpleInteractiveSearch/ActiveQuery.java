package Query.SimpleInteractiveSearch;

import DataStructure.QueryString;
import DataStructure.SuggestionCacheWrapper;
import DataStructure.Trie;
import DocumentModel.EditOperation;
import DocumentModel.IDocument;
import Query.ISuggestionWrapper;
import Query.SuggestionWrapper;

import java.util.*;

public class ActiveQuery {
    Trie<IDocument> queryPosition;
    int previousEdits;
    EditOperation lastEditOperation;
    private QueryString queryString;
    private int queryStringIndex;
    private boolean substitution;

    private final int allowedEditDistance;

    public ActiveQuery(Trie<IDocument> queryPosition, QueryString queryString, int allowedEditDistance){
        this(queryPosition, 0, EditOperation.Match, queryString, 0, false, allowedEditDistance);
    }

    private ActiveQuery(
            Trie<IDocument> queryPosition,
            int previousEdtis,
            EditOperation lastEditOperation,
            QueryString queryString,
            int queryStringIndex,
            boolean isSubstitution,
            int allowedEditDistance)
    {
        this.queryPosition = queryPosition;
        this.previousEdits = previousEdtis;
        this.lastEditOperation = lastEditOperation;
        this.queryString = queryString;
        this.queryStringIndex = queryStringIndex;
        this.allowedEditDistance = allowedEditDistance;
        substitution = isSubstitution;
    }

    public int addCharacter(ArrayList<ActiveQuery> activeQueries){
        Map<Character, Trie<IDocument>> candidateQueryPositions = queryPosition.getChildren();
        int numberOfVisitedNodes = 0;
        for(Character candidatePath : candidateQueryPositions.keySet()){
            Trie<IDocument> newQueryPosition = candidateQueryPositions.get(candidatePath);
            if(candidatePath == queryString.GetCharacter(queryStringIndex)){
                activeQueries.add(new ActiveQuery(
                        newQueryPosition,
                        previousEdits,
                        EditOperation.Match,
                        queryString,
                        queryStringIndex + 1,
                        false,
                        allowedEditDistance));

                numberOfVisitedNodes++;
            }
            else{
                numberOfVisitedNodes += processInsert(newQueryPosition, activeQueries);
            }
        }

        boolean didDelete = processDeletes(activeQueries);
        if(didDelete){
            numberOfVisitedNodes++;
        }

        return numberOfVisitedNodes;
    }

    private int processInsert(Trie<IDocument> newQueryPosition, ArrayList<ActiveQuery> activeQueries)
    {
        int visitedNodes = 0;
        if(isAllowedToInsert()){
            ActiveQuery tempActiveQuery = new ActiveQuery(
                    newQueryPosition,
                    previousEdits + 1,
                    EditOperation.Insert,
                    queryString,
                    queryStringIndex,
                    false,
                    allowedEditDistance);

            visitedNodes++;
            visitedNodes += tempActiveQuery.addCharacter(activeQueries);
        }

        return visitedNodes;
    }

    private boolean isAllowedToInsert() {
        return (lastEditOperation != EditOperation.Delete || substitution) && previousEdits < allowedEditDistance;
    }

    private boolean processDeletes(ArrayList<ActiveQuery> activeQueries) {
        if(isAllowToDelete()){
            int editCost = EditOperation.getOperationCost(lastEditOperation, EditOperation.Delete);
            boolean isSubstitution  = false;
            if(editCost == 0){
                isSubstitution = true;
            }

            activeQueries.add(new ActiveQuery(
                    queryPosition,
                    previousEdits + editCost,
                    EditOperation.Delete,
                    queryString,
                    queryStringIndex + 1,
                    isSubstitution,
                    allowedEditDistance));

            return true;
        }

        return false;
    }

    private boolean isAllowToDelete() {
        return lastEditOperation == EditOperation.Insert || previousEdits < allowedEditDistance;
    }
    /*
    public String toString(){
        return  queryPosition.getLabel() + "Rank: " + queryPosition.getRank() * EditOperation.getRankDiscount(previousEdits) +  ", Last operation: " + lastEditOperation + " edits done: " + previousEdits;
    }*/

    public String getLabel() {
        return queryPosition.getLabel();
    }

    public void getSuggestions(ArrayList<ISuggestionWrapper> suggestions) {
        /*ArrayList<SuggestionCacheWrapper<IDocument>> cachedSuggestions = queryPosition.getCachedSuggestions();
        for (SuggestionCacheWrapper<IDocument> suggestion : cachedSuggestions){
            double suggestionRank = suggestion.getRank() * EditOperation.getRankDiscount(previousEdits);
            boolean hasSuggestion = false;
            for(int i = 0; i < suggestions.size(); i++){
                ISuggestionWrapper suggestionWrapper = suggestions.get(i);
                if(suggestionWrapper.getSuggestion().equals(suggestion.getSuggestion().getLabel())){
                    if(suggestionWrapper.getRank() < suggestionRank){
                        suggestions.set(i, new SuggestionWrapper(suggestion.getSuggestion().getLabel(), suggestionRank));
                    }
                    hasSuggestion = true;
                    break;
                }
            }

            if(!hasSuggestion){
                suggestions.add(new SuggestionWrapper(suggestion.getSuggestion().getLabel(), suggestionRank));
            }
        }*/
    }
}
