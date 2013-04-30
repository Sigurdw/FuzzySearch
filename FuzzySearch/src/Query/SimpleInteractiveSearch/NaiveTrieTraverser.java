package Query.SimpleInteractiveSearch;

import Query.IndexTraverser;
import Query.SuggestionWrapper;

import java.util.ArrayList;
import java.util.Collections;

public class NaiveTrieTraverser implements IndexTraverser {

    private ArrayList<ActiveQuery> activeQueries = new ArrayList<ActiveQuery>();
    private final int numberOfSuggestions;
    private int numberOfNodesInLastIteration = 0;
    private int totalNumberOfNodes = 0;

    public NaiveTrieTraverser(ActiveQuery activeQuery, int numberOfSuggestions){
        this.numberOfSuggestions = numberOfSuggestions;
        activeQueries.add(activeQuery);
    }

    @Override
    public ArrayList<SuggestionWrapper> addCharacter() {
        //System.out.println("Iteration on " + activeQueries.size() + " active nodes.");
        ArrayList<ActiveQuery> nextActiveQueries = new ArrayList<ActiveQuery>();
        for(ActiveQuery activeQuery : activeQueries){
           numberOfNodesInLastIteration += activeQuery.addCharacter(nextActiveQueries);
        }

        ArrayList<SuggestionWrapper> suggestions = new ArrayList<SuggestionWrapper>();
        for(ActiveQuery activeQuery : nextActiveQueries){
            activeQuery.getSuggestions(suggestions);
        }

        Collections.sort(suggestions);

        activeQueries = nextActiveQueries;
        //System.out.println("Iteration completed: " + activeQueries.size());
        totalNumberOfNodes += numberOfNodesInLastIteration;
        return suggestions;
    }

    @Override
    public int getNumberOfNodesInLastIteration() {
        return numberOfNodesInLastIteration;
    }

    @Override
    public int getTotalNodes() {
        return totalNumberOfNodes;
    }

    @Override
    public boolean isQueryExhausted() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void initiateFromExhaustedNodes() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exploreNextNode() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasAvailableSuggestions() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ArrayList<SuggestionWrapper> getAvailableSuggestions(int numberOfSuggestion) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int numberOfRetrievedSuggestions() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
