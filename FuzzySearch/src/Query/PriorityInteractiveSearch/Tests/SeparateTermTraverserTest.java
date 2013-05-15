package Query.PriorityInteractiveSearch.Tests;

import Clustering.ClusteringVector;
import Clustering.TermDocumentVector;
import Config.SearchConfig;
import DataStructure.Index;
import Query.ISuggestionWrapper;
import Query.PriorityInteractiveSearch.TermSeparatedIndexTraverser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class SeparateTermTraverserTest {
    private TermSeparatedIndexTraverser query;

    @Before
    public void setUp(){
        Index index = new Index(1, 1);
        TermDocumentVector vector = new TermDocumentVector(2);
        vector.setDocument(0, 1.1f, 0);
        ClusteringVector clusteringVector = new ClusteringVector(1, 0);
        clusteringVector.Vector[0] = 0.1f;

        index.addTerm("pro", vector, clusteringVector);

        SearchConfig searchConfig = SearchConfig.DummyConfig;
        searchConfig = searchConfig.updateConfig(index);

        query = new TermSeparatedIndexTraverser(searchConfig);
    }

    @Test
    public void getSuggestionsOnEmptyQueryStringTest(){
        query.updateQueryString("p");
        ArrayList<ISuggestionWrapper> suggestionWrappers = getSuggestions();
        Assert.assertEquals(1, suggestionWrappers.size());
        printResults(suggestionWrappers);

        query.updateQueryString("p ");
        suggestionWrappers = getSuggestions();
        Assert.assertEquals(1, suggestionWrappers.size());
        printResults(suggestionWrappers);

        query.updateQueryString("p r");
        suggestionWrappers = getSuggestions();
        printResults(suggestionWrappers);
        Assert.assertEquals(1, suggestionWrappers.size());
    }

    private ArrayList<ISuggestionWrapper> getSuggestions(){
        ArrayList<ISuggestionWrapper> suggestionWrappers = new ArrayList<ISuggestionWrapper>(5);
        while(query.peekNextNodeRank() != -1 &&  suggestionWrappers.size() < 5){
            query.exploreNextNode();
            while(query.peekNextAvailableSuggestionRank() >= query.peekNextNodeRank()){
                ISuggestionWrapper suggestionWrapper = query.getNextAvailableSuggestion();
                suggestionWrappers.add(suggestionWrapper);
            }
        }

        return suggestionWrappers;
    }

    private void printResults(ArrayList<ISuggestionWrapper> suggestionWrappers){
        for(ISuggestionWrapper suggestionWrapper : suggestionWrappers){
            System.out.println(suggestionWrapper);
        }
    }
}
