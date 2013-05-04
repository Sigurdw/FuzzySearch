package Query;

public abstract class ISuggestionWrapper {
    abstract public String getSuggestion();

    abstract public float getRank();

    @Override
    public String toString(){
        return getSuggestion() + ", " + getRank();
    }
}
