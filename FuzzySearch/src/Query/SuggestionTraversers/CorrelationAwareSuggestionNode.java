package Query.SuggestionTraversers;

import DataStructure.TrieNode;
import Query.SuggestionTraversers.SuggestionNode;
import Query.TermCorrelation;

/**
 * User: Sigurd Wien
 * Date: 24.04.13
 * Time: 14:48
 */
public class CorrelationAwareSuggestionNode extends SuggestionNode {
    final float correlationDiscount;

    public CorrelationAwareSuggestionNode(TrieNode[] termStack, TrieNode suggestionPosition) {
        super(suggestionPosition);
        correlationDiscount = TermCorrelation.computeCorrelation(termStack, suggestionPosition);
    }

    @Override
    public float getNextRank(){
        return super.getNextRank() * correlationDiscount;
    }

    @Override
    protected float getRank(){
        return super.getRank() * correlationDiscount;
    }

}
