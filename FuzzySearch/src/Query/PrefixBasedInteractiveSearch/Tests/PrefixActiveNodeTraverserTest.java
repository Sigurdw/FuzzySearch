package Query.PrefixBasedInteractiveSearch.Tests;

import Clustering.ClusteringVector;
import Clustering.TermDocumentVector;
import Config.SearchConfig;
import DataStructure.Index;
import Query.ISuggestionWrapper;
import Query.PrefixBasedInteractiveSearch.PrefixActiveNode;
import Query.PrefixBasedInteractiveSearch.PrefixActiveNodeTraverser;
import Query.PrefixBasedInteractiveSearch.PrefixSuggestionTraverser;
import Query.QueryContext;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * User: Sigurd Wien
 * Date: 12.05.13
 * Time: 02:03
 */
public class PrefixActiveNodeTraverserTest {
    private Index index;
    private SearchConfig searchConfig;

    @Before
    public void setUp(){
        index = new Index(1, 1);
        searchConfig = SearchConfig.DummyConfig;
        searchConfig = searchConfig.updateConfig(index);
    }

    @Test
    public void getSuggestionsOnEmptyQueryStringTest(){
        addTerm("pro", 1.1f);
        PrefixActiveNodeTraverser prefixActiveNodeTraverser = new PrefixActiveNodeTraverser(searchConfig);
        prefixActiveNodeTraverser.initiateFromExhaustedNodes();
        prefixActiveNodeTraverser.updateQueryString("v");
        prefixActiveNodeTraverser.exploreNextNode();
        Assert.assertFalse(prefixActiveNodeTraverser.hasAvailableSuggestions());
        prefixActiveNodeTraverser.exploreNextNode();
        Assert.assertTrue(prefixActiveNodeTraverser.hasAvailableSuggestions());
        ISuggestionWrapper suggestionWrapper = prefixActiveNodeTraverser.getNextAvailableSuggestion();
        Assert.assertFalse(prefixActiveNodeTraverser.hasAvailableSuggestions());
        System.out.println(suggestionWrapper);
    }

    @Test
    public void manyTermsTest(){
        addTerm("pro", 1.1f);
        addTerm("protein", 1.0f);
        addTerm("test", 0.5f);
        addTerm("vase", 0.6f);
        addTerm("people", 0.7f);
        addTerm("photo", 1.2f);

        PrefixActiveNodeTraverser prefixActiveNodeTraverser = new PrefixActiveNodeTraverser(searchConfig);
        prefixActiveNodeTraverser.initiateFromExhaustedNodes();
        prefixActiveNodeTraverser.updateQueryString("vr");

        ArrayList<ISuggestionWrapper> suggestions = getSuggestions(prefixActiveNodeTraverser, 4);
        System.out.println(suggestions);
    }

    private ArrayList<ISuggestionWrapper> getSuggestions(PrefixActiveNodeTraverser traverser, int numberOfSuggestions) {
        ArrayList<ISuggestionWrapper> suggestionWrappers = new ArrayList<ISuggestionWrapper>(numberOfSuggestions);
        while (!traverser.isQueryExhausted() && numberOfSuggestions > suggestionWrappers.size()){
            traverser.exploreNextNode();
            while(traverser.hasAvailableSuggestions() && numberOfSuggestions > suggestionWrappers.size()){
                suggestionWrappers.add(traverser.getNextAvailableSuggestion());
            }
        }

        return suggestionWrappers;
    }

    private void addTerm(String term, float rank){
        TermDocumentVector vector = new TermDocumentVector(1);
        vector.setDocument(0, rank, 0);
        ClusteringVector clusteringVector = new ClusteringVector(1, 0);
        clusteringVector.Vector[0] = 1.0f;

        index.addTerm(term, vector, clusteringVector);
    }
}
