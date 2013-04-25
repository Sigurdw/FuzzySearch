package Query;

import DataStructure.QueryString;
import DataStructure.TrieNode;
import Index.Index;
import Interface.IUpdateInterfaceControl;
import Interface.WorkingStatus;
import Query.PriorityInteractiveSearch.PriorityTrieTraverser;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class InteractiveSearchHandler{

    private String activeQueryString = "";
    private Index index;
    private int numberOfSuggestionsRequired;
    private boolean usePrioritySearch;
    private int allowedEditDistance;
    private final IUpdateInterfaceControl interfaceControl;
    private ITrieTraverser query;
    private ArrayList<ISuggestionWrapper> suggestions = new ArrayList<ISuggestionWrapper>();
    private QueryContext queryContext;
    private TrieNode rootNode;
    private static final String indexPath = "C:/Index/clusteredIndex.dat";

    public InteractiveSearchHandler(
            Index index,
            int numberOfSuggestionsRequired,
            boolean usePrioritySearch,
            int allowedEditDistance,
            IUpdateInterfaceControl interfaceControl)
    {
        this.index = index;
        this.numberOfSuggestionsRequired = numberOfSuggestionsRequired;
        this.usePrioritySearch = usePrioritySearch;
        this.allowedEditDistance = allowedEditDistance;
        this.interfaceControl = interfaceControl;

        try{
            rootNode = TrieNode.read(new DataInputStream(new FileInputStream(new File(indexPath))));
            System.out.println("index loaded");
            ArrayList<ISuggestionWrapper> terms = rootNode.getAllTerms();
            System.out.println("number of terms: " + terms.size());
        }
        catch (Exception e){
            System.exit(1);
        }

        initInteractiveSearch();
    }

    private void initInteractiveSearch() {

        //queryContext = new QueryContext(rootNode, 3, 10);

        //query = new PriorityTrieTraverser(queryContext);
        /*if(usePrioritySearch){
            query = index.initFastSearch(queryString, numberOfSuggestionsRequired, allowedEditDistance);
        }
        else{
            query = index.initSearch(queryString, numberOfSuggestionsRequired, allowedEditDistance);
        }*/
    }

    public void handleUserInputAsync(final String queryString){
        Thread queryThread = new Thread(){
            public void run(){
                interfaceControl.updateWorkingStatus(WorkingStatus.LookingForFirstSuggestions);
                if(!queryString.equals(activeQueryString)){
                    if(queryString.startsWith(activeQueryString)){
                        addCharacter(queryString);
                    }
                    else{
                        initInteractiveSearch();
                        addCharacter(queryString);
                    }

                    activeQueryString = queryString;
                }

                ArrayList<String> suggestions = getSearchResults();
                interfaceControl.updateSuggestionList(suggestions);
                interfaceControl.updateWorkingStatus(WorkingStatus.CalculatingMoreActiveNodes);
            }
        };

        queryThread.start();
    }

    private void addCharacter(String queryStr){
        queryContext.QueryString.SetQueryString(queryStr);
        char lastCharacter = queryContext.QueryString.GetLastCharacter();
        if(lastCharacter != 0){
            //System.out.println("Got: " + lastCharacter);
            suggestions = query.addCharacter();
            //System.out.println(suggestions);
        }
        else{
            suggestions = new ArrayList<ISuggestionWrapper>();
        }
    }

    private ArrayList<String> getSearchResults() {
        ArrayList<String> suggestionStrings = new ArrayList<String>(suggestions.size());
        for (int i = 0; i < Math.min(numberOfSuggestionsRequired, suggestions.size()); i++){
            suggestionStrings.add(suggestions.get(i).toString());
        }

        return suggestionStrings;
    }

    public int getNumberOfNodesInLastIteration(){
        return query.getNumberOfNodesInLastIteration();
    }

    public int getTotalNodes(){
        return query.getTotalNodes();
    }
}
