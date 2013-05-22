package Query.PrefixBasedInteractiveSearch.Tests;

import Clustering.ClusteringVector;
import Clustering.TermDocumentVector;
import Config.SearchConfig;
import DataStructure.Index;
import Query.QueryContext;
import org.junit.Before;
import org.junit.Test;

/**
 * User: Sigurd Wien
 * Date: 12.05.13
 * Time: 00:44
 */
public class PrefixActiveNodeTest {
    private Index index;
    private QueryContext queryContext;

    @Before
    public void setUp(){
        index = new Index(1, 1);
        TermDocumentVector vector = new TermDocumentVector(2);
        vector.setDocument(0, 1.1f, 0);
        ClusteringVector clusteringVector = new ClusteringVector(1, 0);
        clusteringVector.Vector[0] = 0.1f;

        index.addTerm("pro", vector, clusteringVector);
        SearchConfig searchConfig = SearchConfig.DummyConfig;
        searchConfig = searchConfig.updateConfig(index);
        queryContext = new QueryContext(searchConfig);
    }

    @Test
    public void getSuggestionsOnEmptyQueryStringTest(){
        /*PrefixActiveNode prefixActiveNode = new PrefixActiveNode(queryContext, 0);
        PrefixSuggestionTraverser suggestionTraverser = prefixActiveNode.getSuggestionTraverser();
        ISuggestionWrapper suggestion = suggestionTraverser.getNextSuggestion(0);
        System.out.println(suggestion);

        queryContext.QueryString.appendCharacter('e');

        PrefixActiveNode nextNode = prefixActiveNode.getNextChildNodes();
        suggestionTraverser = nextNode.getSuggestionTraverser();
        suggestion = suggestionTraverser.getNextSuggestion(0);
        System.out.println(suggestion);

        queryContext.QueryString.appendCharacter('t');

        nextNode = nextNode.getNextChildNodes();
        suggestionTraverser = nextNode.getSuggestionTraverser();
        suggestion = suggestionTraverser.getNextSuggestion(0);
        System.out.println(suggestion);*/
    }
}
