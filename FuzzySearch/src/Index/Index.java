package Index;

import DataStructure.QueryString;
import DataStructure.Trie;
import DocumentModel.IDocument;
import Query.*;
import Query.FastInteractiveSearch.FastActiveNode;
import Query.FastInteractiveSearch.FastTrieTraverser;
import Query.NaiveInteractiveSearch.ActiveQuery;
import Query.NaiveInteractiveSearch.NaiveTrieTraverser;

import java.util.ArrayList;
import java.util.HashSet;

public class Index {
    private Trie<IDocument> indexImplementation;
    private HashSet<String> indexTerms;

    public Index(Trie<IDocument> indexImplementation, HashSet<String> indexTerms){
        this.indexImplementation = indexImplementation;
        this.indexTerms = indexTerms;
    }

    public void putIndexTerms(ArrayList<String> indexTermsList){
        indexTerms.addAll(indexTermsList);
    }

    public ArrayList<IDocument> search(String key){
        System.out.println("Searching the index for: " + key);
        return indexImplementation.search(key);
    }

    public ArrayList<String> getSuggestions(String partialKey){
        System.out.println("Getting suggestions for: " + partialKey);
        return indexImplementation.getSuggestions(partialKey);
    }

    public NaiveTrieTraverser initSearch(
            QueryString queryString,
            int numberOfSuggestions,
            int allowedEditDistance)
    {
        ActiveQuery activeQuery = new ActiveQuery(indexImplementation, queryString, allowedEditDistance);
        return new NaiveTrieTraverser(activeQuery, numberOfSuggestions);
    }

    public ITrieTraverser initFastSearch(QueryString queryString, int numberOfSuggestions, int allowedEditDistance) {
        FastActiveNode activeNode = new FastActiveNode(indexImplementation, queryString, allowedEditDistance);
        return new FastTrieTraverser(activeNode, queryString, numberOfSuggestions);
    }

    public ArrayList<String> getRandomIndexTerms(int maxNumberOfTerms){
        int actualNumberOfTerms = Math.min(maxNumberOfTerms, indexTerms.size());
        ArrayList<String> randomTerms = new ArrayList<String>(actualNumberOfTerms);
        for(int i = 0; i < actualNumberOfTerms; i++){
            randomTerms.add(getRandomIndexTerm());
        }

        return randomTerms;
    }

    public String getRandomIndexTerm(){
        Object[] indexTermArray = indexTerms.toArray();
        int index = (int)(Math.random() * indexTermArray.length);
        return (String)indexTermArray[index];
    }
}
