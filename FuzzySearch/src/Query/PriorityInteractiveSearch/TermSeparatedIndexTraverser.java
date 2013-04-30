package Query.PriorityInteractiveSearch;

import Query.IndexTraverser;
import Query.QueryContext;
import Query.SuggestionWrapper;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * User: Sigurd Wien
 * Date: 24.04.13
 * Time: 21:29
 */

public class TermSeparatedIndexTraverser implements IndexTraverser {
    public final char separator = ' ';
    private final ArrayList<PriorityTrieTraverser> traversers = new ArrayList<PriorityTrieTraverser>();
    private final QueryContext queryContext;

    private final PriorityQueue<SuggestionSet> suggestionSets = new PriorityQueue<SuggestionSet>();

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
    public ArrayList<SuggestionWrapper> getAvailableSuggestions(int numberOfSuggestion) {
		for(PriorityTrieTraverser traverser : traversers){
		    traverser.getAvailableSuggestions()
		}
    }

    private SuggestionSet makeNextSet(){

    }

    private float getNextSetRank(){

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
    	
    	private final SuggestionWrapper[] suggestionSet;
    	
    	public SuggestionSet(int numberOfTerms){
    		suggestionSet = new SuggestionWrapper[numberOfTerms];
    	}
    	
    	public void addTerm(SuggestionWrapper term, int index){
    		suggestionSet[index] = term;
    	}
    	
    	public void calculateRankEstimate(){
    		rankEstimate = 0;
    		for(SuggestionWrapper suggestionWrapper : suggestionSet){
    			rankEstimate += suggestionWrapper.getRank();
    		}

            rankEstimate /= suggestionSet.length;
    	}

    	public int compareTo(SuggestionSet otherSuggestionSet){
            float difference = otherSuggestionSet.rankEstimate - this.rankEstimate;
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
}
