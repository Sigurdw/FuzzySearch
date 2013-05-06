package DataStructure;

public class QueryString {
    private String queryString;

    public QueryString(){
        queryString = "";
    }

    public void SetQueryString(String queryString){
        this.queryString = queryString;
    }

    public void appendCharacter(char character){
        queryString = queryString + character;
    }

    public char GetCharacter(int queryStringIndex){
        char character = 0;
        if(!IsExhausted(queryStringIndex)){
            character = queryString.charAt(queryStringIndex);
        }

        return character;
    }

    public boolean IsExhausted(int queryStringIndex){
        return queryString.length() <= queryStringIndex || queryStringIndex < 0;
    }

    public int GetLength(){
        return queryString.length();
    }

    public char GetLastCharacter(){
        char lastCharacter = 0;
        if(queryString.length() > 0){
            lastCharacter = queryString.charAt(queryString.length() - 1);
        }

        return lastCharacter;
    }
}
