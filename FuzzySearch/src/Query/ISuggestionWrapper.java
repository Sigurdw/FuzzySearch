package Query;

public abstract class ISuggestionWrapper implements Comparable<ISuggestionWrapper> {
    abstract public String getSuggestion();

    abstract public float getRank();

    @Override
    public String toString(){
        return getSuggestion() + ", " + getRank();
    }

    @Override
    public int compareTo(ISuggestionWrapper otherSuggestion) {
        float difference = otherSuggestion.getRank() - this.getRank();
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
