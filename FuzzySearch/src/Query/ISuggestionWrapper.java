package Query;

public interface ISuggestionWrapper extends Comparable<ISuggestionWrapper>{
    public String getSuggestion();
    public double getRank();
}
