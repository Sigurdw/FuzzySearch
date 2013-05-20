package Query.PriorityInteractiveSearch;

import DataStructure.TrieNode;
import Query.ISuggestionWrapper;
import java.util.PriorityQueue;

public final class SuggestionTraverser implements Comparable<SuggestionTraverser>{
    private final PriorityQueue<SuggestionNode> suggestionNodeQueue = new PriorityQueue<SuggestionNode>();
    private final TrieNode[] previousTerms;
    private final SuggestionNodeRegister suggestionNodeRegister;
    private final float editDiscount;

    public SuggestionTraverser(
            TrieNode rootNode,
            SuggestionNodeRegister suggestionNodeRegister,
            TrieNode[] previousTerms,
            float editDiscount)
    {
        this.suggestionNodeRegister = suggestionNodeRegister;
        this.previousTerms = previousTerms;
        this.editDiscount = editDiscount;
        SuggestionNode suggestionNode = suggestionNodeRegister.getSuggestionNode(previousTerms, rootNode);

        if (suggestionNode != null){
            suggestionNodeQueue.add(suggestionNode);
        }
    }

    public ISuggestionWrapper getNextSuggestion(){
        SuggestionNode leafSuggestionNode = getNextLeafSuggestionNode();
        return leafSuggestionNode != null ? leafSuggestionNode.getSuggestion(editDiscount, previousTerms) : null;
    }

    private SuggestionNode getNextLeafSuggestionNode(){
        while(suggestionNodeQueue.size() > 0){
            SuggestionNode suggestionNode = suggestionNodeQueue.poll();
            if(suggestionNode.isLeaf()){
                return suggestionNode;
            }
            else{
                exploreChildrenNodes(suggestionNode);
            }
        }

        return null;
    }

    private void exploreChildrenNodes(SuggestionNode suggestionNode) {
        SuggestionNode nextSuggestionNodeChild = null;
        while(nextSuggestionNodeChild == null && suggestionNode.hasMoreChildren()){
            TrieNode nextChild = suggestionNode.getNextChild();
            nextSuggestionNodeChild = suggestionNodeRegister.getSuggestionNode(previousTerms, nextChild);
        }

        if(nextSuggestionNodeChild != null){
            suggestionNodeQueue.add(nextSuggestionNodeChild);
        }

        if(suggestionNode.hasMoreChildren()){
            suggestionNodeQueue.add(suggestionNode);
        }
    }

    public float getNextRank(){
        float rank = -2;
        SuggestionNode suggestionNode = getNextLeafSuggestionNode();
        if(suggestionNode != null){
            rank = suggestionNode.getNextRank() * editDiscount;
            suggestionNodeQueue.add(suggestionNode);
        }

        return rank;
    }

    @Override
    public int compareTo(SuggestionTraverser o) {
        float difference = o.getNextRank() - this.getNextRank();
        if(difference > 0){
            return 1;
        }
        else if(difference < 0){
            return -1;
        }
        else{
            return 0;
        }
    }
}
