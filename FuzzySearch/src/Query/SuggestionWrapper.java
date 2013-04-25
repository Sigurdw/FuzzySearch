package Query;

public class SuggestionWrapper implements ISuggestionWrapper{

    private String suggestion;
    private double rank;

    public SuggestionWrapper(String suggestion, double rank){

        this.suggestion = suggestion;
        this.rank = rank;
    }

    @Override
    public String getSuggestion() {
        return suggestion;
    }

    @Override
    public double getRank() {
        return rank;
    }

    @Override
    public int compareTo(ISuggestionWrapper o) {
        double difference = o.getRank()- rank;
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

    public String toString(){
        return suggestion + ", " + rank;
    }
}
