package Query;

import Clustering.TermDocumentVector;
import DataStructure.LeafTrieNode;
import DataStructure.TrieNode;

/**
 * User: Sigurd Wien
 * Date: 23.04.13
 * Time: 16:28
 */
public final class TermCorrelation {
    public static float computeCorrelation(TrieNode[] termStack, TrieNode term){
        for(TrieNode previousTerm : termStack){
            LeafTrieNode previousTermLeaf = (LeafTrieNode)previousTerm;

        }

        return 0;
    }

}
