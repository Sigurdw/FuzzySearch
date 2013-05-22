package Query.SuggestionTraversers;

import DataStructure.TrieNode;
import Query.ISuggestionWrapper;

import java.util.PriorityQueue;

/**
 * User: Sigurd Wien
 * Date: 11.05.13
 * Time: 22:06
 */
public class PrefixSuggestionTraverser implements Comparable<PrefixSuggestionTraverser> {
    private final PriorityQueue<SuggestionNode> suggestionNodeQueue = new PriorityQueue<SuggestionNode>();
    private final TrieNode[] previousTerms;
    private final float editDiscount;

    public PrefixSuggestionTraverser(
            TrieNode rootNode,
            TrieNode[] previousTerms,
            float editDiscount)
    {
        this.previousTerms = previousTerms;
        this.editDiscount = editDiscount;
        SuggestionNode suggestionNode = new SuggestionNode(rootNode);
        suggestionNodeQueue.add(suggestionNode);
    }

    public ISuggestionWrapper getNextSuggestion(float lowerRankThreshold){
        ISuggestionWrapper suggestionWrapper = null;
        while(hasGoodEnoughSuggestions(lowerRankThreshold))
        {
            SuggestionNode suggestionNode = suggestionNodeQueue.poll();
            if(suggestionNode.isLeaf()){
                suggestionWrapper = suggestionNode.getSuggestion(editDiscount, previousTerms);
                break;
            }
            else{
                TrieNode nextChild = suggestionNode.getNextChild();
                SuggestionNode nextSuggestionNode = new SuggestionNode(nextChild);
                suggestionNodeQueue.add(nextSuggestionNode);

                if(suggestionNode.hasMoreChildren()){
                    suggestionNodeQueue.add(suggestionNode);
                }
            }
        }

        return suggestionWrapper;
    }

    private boolean hasGoodEnoughSuggestions(double lowerRankLimit) {
        SuggestionNode suggestionNode = suggestionNodeQueue.peek();
        return suggestionNode != null && suggestionNode.getNextRank() >= lowerRankLimit;
    }

    public float getNextRank(){
        SuggestionNode suggestionNode = suggestionNodeQueue.peek();
        if(suggestionNode != null){
            return suggestionNode.getNextRank() * editDiscount;
        }

        return -2;
    }

    @Override
    public int compareTo(PrefixSuggestionTraverser o) {
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
