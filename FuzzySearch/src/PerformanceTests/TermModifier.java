package PerformanceTests;

import java.util.ArrayList;

public class TermModifier {

    public static ArrayList<String> introduceModifications(ArrayList<String> indexTerms, int numberOfModifications){
        ArrayList<String> modifiedIndexTerms = new ArrayList<String>(indexTerms.size());
        for(String indexTerm : indexTerms){
            String modifiedTerm = modifyTerm(numberOfModifications, indexTerm);
            modifiedIndexTerms.add(modifiedTerm);
        }

        return modifiedIndexTerms;
    }

    public static String modifyTerm(int numberOfModifications, String indexTerm) {
        String modifiedTerm = indexTerm;
        for(int i = 0; i < numberOfModifications; i++){
            int method = (int)(Math.random() * 3);
            int index = (int)(Math.random() * modifiedTerm.length());
            switch (method){
                case 0:
                    modifiedTerm = deleteLetter(modifiedTerm, index);
                    break;
                case 1:
                    modifiedTerm = replaceWithRandomLetter(modifiedTerm, index);
                    break;
                case 2:
                    modifiedTerm = insertRandomLetter(modifiedTerm, index);
                    break;
            }
        }

        return modifiedTerm;
    }

    public static String scrambleTerm(String original){
        String modifiedTerm = original;
        for(int i = 0; i < original.length(); i++){
            modifiedTerm = replaceWithRandomLetter(modifiedTerm, i);
        }

        return modifiedTerm;
    }

    private static String deleteLetter(String original, int index){
        if(original.length() > 0)
            return original.substring(0, index) + original.substring(index + 1, original.length());
        else{
            return original;
        }
    }

    private static String replaceWithRandomLetter(String original, int index){
        char randomChar = getRandomChar();
        return original.substring(0, index) + randomChar + original.substring(index + 1, original.length());
    }

    private static String insertRandomLetter(String original, int index){
        char randomChar = getRandomChar();
        return original.substring(0, index) + randomChar + original.substring(index, original.length());
    }

    private static char getRandomChar(){
        return (char)('a' + (int)(Math.random() * 26));
    }
}
