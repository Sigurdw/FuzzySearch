package Query.PriorityInteractiveSearch.Tests;

import Clustering.ClusteringVector;
import Clustering.TermDocumentVector;
import Config.SearchConfig;
import DataStructure.Index;
import Query.ISuggestionWrapper;
import Query.PriorityInteractiveSearch.PriorityTrieTraverser;
import Query.PriorityInteractiveSearch.TermSeparatedIndexTraverser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class SeparateTermTraverserTest {
    TermSeparatedIndexTraverser query;

    @Before
    public void setUp(){
        /*index = new Index(2, 2);
        TermDocumentVector vector = new TermDocumentVector(2);
        vector.setDocument(0, 1.0f, 0);
        vector.setDocument(1, 0.5f, 0);
        ClusteringVector clusteringVector = new ClusteringVector(2, 1);
        clusteringVector.Vector[0] = 1.0f;
        clusteringVector.Vector[1] = 0.5f;

        index.addTerm("protein", vector, clusteringVector);
        vector = new TermDocumentVector(2);
        vector.setDocument(0, 0, 0);
        vector.setDocument(1, 1.1f, 0);
        clusteringVector = new ClusteringVector(2, 0);
        clusteringVector.Vector[0] = 0.1f;
        clusteringVector.Vector[1] = 0.9f;

        index.addTerm("test", vector, clusteringVector);

        vector.setDocument(0, 0, 0);
        vector.setDocument(1, 1.1f, 0);
        clusteringVector = new ClusteringVector(2, 0);
        clusteringVector.Vector[0] = 0.1f;
        clusteringVector.Vector[1] = 0.9f;

        index.addTerm("rotein", vector, clusteringVector);

        vector.setDocument(0, 0, 0);
        vector.setDocument(1, 1.1f, 0);
        clusteringVector = new ClusteringVector(2, 0);
        clusteringVector.Vector[0] = 0.1f;
        clusteringVector.Vector[1] = 0.9f;

        index.addTerm("pro", vector, clusteringVector);

        SearchConfig searchConfig = SearchConfig.DummyConfig;
        searchConfig = searchConfig.updateConfig(index);

        query = new PriorityTrieTraverser(searchConfig);*/

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

        query.updateQueryString("p ");
        suggestionWrappers = getSuggestions();
        Assert.assertEquals(1, suggestionWrappers.size());

        query.updateQueryString("p r");
        suggestionWrappers = getSuggestions();
        Assert.assertEquals(1, suggestionWrappers.size());
    }

    private ArrayList<ISuggestionWrapper> getSuggestions(){
        ArrayList<ISuggestionWrapper> suggestionWrappers = new ArrayList<ISuggestionWrapper>(5);
        while(!query.isQueryExhausted() &&  suggestionWrappers.size() < 5){
            query.exploreNextNode();
            while(query.hasAvailableSuggestions()){
                ISuggestionWrapper suggestionWrapper = query.getNextAvailableSuggestion();
                if(suggestionWrapper != null){
                    suggestionWrappers.add(suggestionWrapper);
                }
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
