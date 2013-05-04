package DataStructure;

public class SuggestionCacheWrapper<T> implements Comparable<SuggestionCacheWrapper<T>> {

    private Trie<T> suggestion;

    public SuggestionCacheWrapper(Trie<T> suggestion){
        this.suggestion = suggestion;
    }

    public Trie<T> getSuggestion(){
        return suggestion;
    }

    public double getRank(){
        return suggestion.getSelfRank();
    }

    public int compareTo(SuggestionCacheWrapper<T> suggestionWrapper) {
        double difference = suggestionWrapper.getRank() - getRank();
        if(difference == 0){
            return 0;
        }
        else if(difference > 0){
            return 1;
        }
        else{
            return -1;
        }
    }

    public boolean equals(Object o){
        return (o instanceof SuggestionCacheWrapper) && (((SuggestionCacheWrapper<T>) o).getSuggestion() == getSuggestion());

    }

    public String toString(){
        return suggestion.getLabel() + ", " + getRank();
    }
}
