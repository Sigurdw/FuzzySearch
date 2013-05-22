package Query.PrefixBasedInteractiveSearch.Tests;

import Clustering.ClusteringVector;
import Clustering.TermDocumentVector;
import Config.SearchConfig;
import DataStructure.Index;
import Query.ISuggestionWrapper;
import Query.IndexTraverser;
import Query.PrefixBasedInteractiveSearch.PrefixActiveNodeTraverser;
import Query.PriorityInteractiveSearch.PriorityTrieTraverser;
import Query.TermSeparatedInteractiveSearch.TermSeparatedIndexTraverser;
import Query.SimpleInteractiveSearch.SimpleIndexTraverser;
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
        prefixActiveNodeTraverser.updateQueryString("v");
        prefixActiveNodeTraverser.exploreNextNode();
        Assert.assertFalse(hasAvailableSuggestions(prefixActiveNodeTraverser));
        prefixActiveNodeTraverser.exploreNextNode();
        Assert.assertTrue(hasAvailableSuggestions(prefixActiveNodeTraverser));
        ISuggestionWrapper suggestionWrapper = prefixActiveNodeTraverser.getNextAvailableSuggestion();
        Assert.assertFalse(hasAvailableSuggestions(prefixActiveNodeTraverser));
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
        prefixActiveNodeTraverser.updateQueryString("vr");

        ArrayList<ISuggestionWrapper> suggestions = getSuggestions(prefixActiveNodeTraverser, 6);
        System.out.println(suggestions);
        Assert.assertEquals(3, suggestions.size());
    }

    @Test
    public void longerQueryThanTermTest(){
        addTerm("p", 1.1f);
        PrefixActiveNodeTraverser prefixActiveNodeTraverser = new PrefixActiveNodeTraverser(searchConfig);
        prefixActiveNodeTraverser.updateQueryString("pr");

        ArrayList<ISuggestionWrapper> suggestions = getSuggestions(prefixActiveNodeTraverser, 3);
        System.out.println(suggestions);
    }

    @Test
    public void fuzzyHighestRankTest(){
        addTerm("le", 1.1f);
        PrefixActiveNodeTraverser prefixActiveNodeTraverser = new PrefixActiveNodeTraverser(searchConfig);
        prefixActiveNodeTraverser.updateQueryString("c");

        ArrayList<ISuggestionWrapper> suggestions = getSuggestions(prefixActiveNodeTraverser, 3);
        System.out.println(suggestions);
        Assert.assertEquals(1, suggestions.size());
        Assert.assertEquals(0.5f, suggestions.get(0).getRank());
    }

    @Test
    public void strangeBehaviourTest(){
        addTerm("roph", 1.0f);
        String queryString = "oph";

        PrefixActiveNodeTraverser prefixActiveNodeTraverser = new PrefixActiveNodeTraverser(searchConfig);
        prefixActiveNodeTraverser.updateQueryString(queryString);
        ArrayList<ISuggestionWrapper> suggestions = getSuggestions(prefixActiveNodeTraverser, 3);
        System.out.println(suggestions);


        PriorityTrieTraverser priorityTrieTraverser = new PriorityTrieTraverser(searchConfig);
        priorityTrieTraverser.updateQueryString(queryString);
        suggestions = getSuggestions(priorityTrieTraverser, 3);
        System.out.println(suggestions);

        SimpleIndexTraverser simpleIndexTraverser = new SimpleIndexTraverser(searchConfig);
        simpleIndexTraverser.updateQueryString(queryString);
        suggestions = getSuggestions(simpleIndexTraverser, 3);
        System.out.println(suggestions);
    }

    @Test
    public void theProblemTest(){
        addTerm("pro", 1.0f);
        PrefixActiveNodeTraverser prefixActiveNodeTraverser = new PrefixActiveNodeTraverser(searchConfig);
        String queryString = "ro";
        prefixActiveNodeTraverser.updateQueryString(queryString);
        ArrayList<ISuggestionWrapper> suggestions = getSuggestions(prefixActiveNodeTraverser, 3);
        System.out.println(suggestions);
    }

    @Test
    public void tooEagerMultiTermBugTest(){
        addTerm("alu", 1.0f);
        addTerm("le", 1.0f);
        String queryString = "nalu";

        PrefixActiveNodeTraverser prefixActiveNodeTraverser = new PrefixActiveNodeTraverser(searchConfig);
        prefixActiveNodeTraverser.updateQueryString(queryString);
        ArrayList<ISuggestionWrapper> suggestions = getSuggestions(prefixActiveNodeTraverser, 5);
        System.out.println(suggestions);
        Assert.assertEquals(1, suggestions.size());
    }

    @Test
    public void tooLittleEagerMultiTermBugTest(){
        addTerm("mi", 1.0f);
        addTerm("le", 1.0f);
        String queryString = "m ";

        PrefixActiveNodeTraverser prefixActiveNodeTraverser = new PrefixActiveNodeTraverser(searchConfig);
        prefixActiveNodeTraverser.updateQueryString(queryString);
        ArrayList<ISuggestionWrapper> suggestions = getSuggestions(prefixActiveNodeTraverser, 5);
        System.out.println(suggestions);
        Assert.assertEquals(3, suggestions.size());
    }

    @Test
    public void tooMuchEditDistanceOnSimpleMultiTerm(){
        addTerm("mary", 1.0f);
        addTerm("le", 1.0f);
        SimpleIndexTraverser simpleIndexTraverser = new SimpleIndexTraverser(searchConfig);
        String queryString = "mary*";
        for(int i = 1; i <= queryString.length(); i++){
            String currentQuery = queryString.substring(0, i);
            System.out.println(currentQuery);
            simpleIndexTraverser.updateQueryString(currentQuery);
            ArrayList<ISuggestionWrapper> suggestions = getSuggestions(simpleIndexTraverser, 3);
            System.out.println(suggestions);
            //Assert.assertEquals(1, suggestions.size());
        }

        TermSeparatedIndexTraverser termSeparatedIndexTraverser = new TermSeparatedIndexTraverser(searchConfig);
        for(int i = 1; i <= queryString.length(); i++){
            String currentQuery = queryString.substring(0, i);
            System.out.println(currentQuery);
            termSeparatedIndexTraverser.updateQueryString(currentQuery);
            ArrayList<ISuggestionWrapper> suggestions = getSuggestions(termSeparatedIndexTraverser, 3);
            System.out.println(suggestions);
            //Assert.assertEquals(1, suggestions.size());
        }
    }

    private boolean hasAvailableSuggestions(IndexTraverser indexTraverser){
        return indexTraverser.peekNextAvailableSuggestionRank() >= indexTraverser.peekNextNodeRank();
    }

    private ArrayList<ISuggestionWrapper> getSuggestions(IndexTraverser traverser, int numberOfSuggestions) {
        ArrayList<ISuggestionWrapper> suggestionWrappers = new ArrayList<ISuggestionWrapper>(numberOfSuggestions);
        while (traverser.peekNextNodeRank() != -1 && numberOfSuggestions > suggestionWrappers.size()){
            traverser.exploreNextNode();
            while(hasAvailableSuggestions(traverser) && numberOfSuggestions > suggestionWrappers.size()){
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
