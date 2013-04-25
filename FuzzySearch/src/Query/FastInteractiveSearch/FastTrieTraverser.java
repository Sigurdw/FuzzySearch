package Query.FastInteractiveSearch;

import DataStructure.QueryString;
import Query.*;
import Query.FastInteractiveSearch.Links.Link;
import Query.FastInteractiveSearch.Links.SuggestionLink;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class FastTrieTraverser implements ITrieTraverser {

    private QueryString queryString;
    private int neededSuggestions;
    private PriorityQueue<Link> linkQueue = new PriorityQueue<Link>();
    private ArrayList<FastActiveNode> exhaustedNodes = new ArrayList<FastActiveNode>();
    private int currentNodeVisitations = 0;
    private int totalNodeVisitations = 0;

    public FastTrieTraverser(FastActiveNode rootActiveNode, QueryString queryString, int neededSuggestions){
        this.queryString = queryString;
        this.neededSuggestions = neededSuggestions;
        exhaustedNodes.add(rootActiveNode);
    }

    public ArrayList<ISuggestionWrapper> addCharacter() {
        ArrayList<ISuggestionWrapper> suggestions = new ArrayList<ISuggestionWrapper>();
        initiateFromExhaustedNodes();
        Link nextLink = getNextLink();
        currentNodeVisitations = 1;
        while(neededSuggestions - suggestions.size() > 0 && nextLink != null){
            FastActiveNode currentNode = nextLink.UseLink(linkQueue);
            if(currentNode.isNew()){
                currentNodeVisitations++;
            }
            if(currentNode.isExhausted()){
                double thresholdRank = 0;
                Link thresholdLink = linkQueue.peek();
                if(thresholdLink != null){
                    thresholdRank = thresholdLink.getRank();
                }

                currentNode.getSuggestions(
                        suggestions,
                        neededSuggestions - suggestions.size(),
                        thresholdRank);
                SuggestionLink suggestionLink = currentNode.getSuggestionLink();
                if(suggestionLink != null){
                    linkQueue.add(suggestionLink);
                }

                if(!exhaustedNodes.contains(currentNode)){
                    exhaustedNodes.add(currentNode);
                }
            }
            else{
                currentNode.extractLinks(linkQueue);
            }

            if(suggestions.size() >= neededSuggestions){
                break;
            }

            nextLink = getNextLink();
        }

        totalNodeVisitations += currentNodeVisitations;
        //System.out.println("Done in " + currentNodeVisitations);
        return suggestions;
    }

    @Override
    public int getNumberOfNodesInLastIteration() {
        return currentNodeVisitations;
    }

    @Override
    public int getTotalNodes() {
        return totalNodeVisitations;
    }

    private Link getNextLink() {
        Link nextLink = linkQueue.poll();
        while(nextLink != null && !nextLink.isValid(queryString)){
            nextLink = linkQueue.poll();
        }

        return nextLink;
    }

    private void initiateFromExhaustedNodes() {
        for(FastActiveNode exhaustedNode : exhaustedNodes){
            exhaustedNode.extractLinks(linkQueue);
        }

        exhaustedNodes.clear();
    }
}
