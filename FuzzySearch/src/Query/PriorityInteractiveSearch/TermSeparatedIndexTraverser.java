package Query.PriorityInteractiveSearch;

import Query.ISuggestionWrapper;
import Query.QueryContext;
import java.util.ArrayList;

/**
 * User: Sigurd Wien
 * Date: 24.04.13
 * Time: 21:29
 */

public class TermSeparatedIndexTraverser implements IndexTraverser {
    public final char separator = ' ';
    private final ArrayList<PriorityTrieTraverser> traversers = new ArrayList<PriorityTrieTraverser>();
    private final QueryContext queryContext;

    public TermSeparatedIndexTraverser(QueryContext queryContext){
        this.queryContext = queryContext;
        traversers.add(new PriorityTrieTraverser(queryContext));
    }

    @Override
    public boolean isQueryExhausted() {
        return getCurrentTraverser().isQueryExhausted();
    }

    @Override
    public void initiateFromExhaustedNodes() {
        getCurrentTraverser().isQueryExhausted();
    }

    @Override
    public void exploreNextNode() {
        if(queryContext.QueryString.GetLastCharacter() == separator){
            traversers.add(new PriorityTrieTraverser(queryContext));
        }
        else{
            getCurrentTraverser().exploreNextNode();
        }
    }

    @Override
    public boolean hasAvailableSuggestions() {
    	
    }

    @Override
    public ArrayList<ISuggestionWrapper> getAvailableSuggestions(int numberOfSuggestion) {
		for(PriorityTrieTraverser traverser : traversers){
			
		}
    }
    
    

    @Override
    public int numberOfRetrievedSuggestions() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private PriorityTrieTraverser getCurrentTraverser(){
        return traversers.get(traversers.size() - 1);
    }
    
    private final class SuggestionSet implements Comparable<SuggestionSet>
    {
    	private float rankEstimate;
    	
    	private final ISuggestionWrapper[] suggestionSet;
    	
    	public SuggestionSet(int numberOfTerms){
    		suggestionSet = new SuggestionSet[numberOfTerms];
    	}
    	
    	public void addTerm(ISuggestionWrapper term, int index){
    		suggestionSet[index] = term;
    	}
    	
    	public void calculateRankEstimate(){
    		rankEstimate = 0;
    		for(ISuggestionWrapper suggestionWrapper : suggestionSet){
    			rankEstimate += suggestionWrapper.getRank() / suggestionSet.length;
    		}
    	}
    	
    
    
    	public int compareTo(SuggestionSet otherSuggestionSet){
    	
    	}	
    }
}
