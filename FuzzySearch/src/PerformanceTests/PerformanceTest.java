package PerformanceTests;

import Interface.IUpdateInterfaceControl;
import Interface.WorkingStatus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PerformanceTest {
    /*private static final String directoryPath = "C:/TextCollection";
    private static final int numberOfSuggestionsRequired = 20;
    private static final int allowedEditDistance = 2;
    private Index index;
    private Index priorityIndex;
    private InteractiveSearchHandler searchHandler;
    private static final String resultPath = "D:/Results/";
    private static final String fileEnding  = ".csv";
    private final IUpdateInterfaceControl interfaceControl = new DummyInterface();

    public PerformanceTest(int numberOfDicuments){
        Indexer naiveIndexer = new Indexer(numberOfSuggestionsRequired, true);
        try {
            index = naiveIndexer.indexDocumentBlob(new File(directoryPath + "/medline2004.txt"), numberOfDicuments);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        //Indexer priorityIndexer = new Indexer(numberOfSuggestionsRequired, true);
        //priorityIndex =
    }

    public void editDistanceScalingTest(){
        int[] numberOfTerms = {10000, 10000, 1000, 50};
        ArrayList<String> terms = index.getRandomIndexTerms(10000);
        doEditDistanceScaleTest(numberOfTerms, terms, "real");
    }

    public ArrayList<String> getIndexTerms(int number){
        return index.getRandomIndexTerms(number);
    }

    public void plainSearchTest(BufferedWriter simpleWriter, BufferedWriter priorityWriter) throws IOException{
        int editDistance = 2;
        ArrayList<String> terms = getIndexTerms(1000);

        for(int j = 0; j < terms.size(); j++){
            String term = terms.get(j);
            String modifiedTerm = TermModifier.modifyTerm(editDistance, term);
            searchHandler = new InteractiveSearchHandler(index, 10, false, editDistance, interfaceControl);
            long simpleTime = doInteractiveSearch(modifiedTerm);
            searchHandler = new InteractiveSearchHandler(index, 10, true, editDistance, interfaceControl);
            long priorityTime = doInteractiveSearch(modifiedTerm);

            String simpleRecord = "" + simpleTime;
            String priorityRecord = "" + priorityTime;
            if(j == terms.size() - 1){
                simpleRecord += "\n";
                priorityRecord += "\n";
            }
            else{
                simpleRecord += "; ";
                priorityRecord += "; ";
            }

            simpleWriter.write(simpleRecord);
            priorityWriter.write(priorityRecord);
        }

        simpleWriter.flush();
        priorityWriter.flush();
    }

    public void randomCharacterTest(){
        int[] numberOfTerms = {10000, 10000, 1000, 50};
        ArrayList<String> terms = index.getRandomIndexTerms(10000);
        for(int i = 0; i < terms.size(); i++){
            terms.set(i, TermModifier.scrambleTerm(terms.get(i)));
        }

        doEditDistanceScaleTest(numberOfTerms, terms, "scramble");
    }

    public void kScalingTest(){
        int editDistance = 2;
        ArrayList<String> terms = index.getRandomIndexTerms(1000);
        try {
            //File simpleAverageResultFile = new File(resultPath + "simpleKScaling1" + fileEnding);
            File priorityAverageResultFile = new File(resultPath + "priorityKScaling2" + fileEnding);
            //BufferedWriter simpleWriter = new BufferedWriter(new FileWriter(simpleAverageResultFile));
            BufferedWriter priorityWriter = new BufferedWriter(new FileWriter(priorityAverageResultFile));
            for(int k = 1; k <= numberOfSuggestionsRequired; k++){
                System.out.println("kScaling: " + k);
                for(int j = 0; j < terms.size(); j++){
                    String term = terms.get(j);
                    String modifiedTerm = TermModifier.modifyTerm(editDistance, term);
                    //searchHandler = new InteractiveSearchHandler(index, k, false, editDistance);
                    //long simpleTime = doInteractiveSearch(modifiedTerm);
                    searchHandler = new InteractiveSearchHandler(index, k, true, editDistance, interfaceControl);
                    long priorityTime = doInteractiveSearch(modifiedTerm);

                    //String simpleRecord = "" + simpleTime;
                    String priorityRecord = "" + priorityTime;
                    if(j == terms.size() - 1){
                     //   simpleRecord += "\n";
                        priorityRecord += "\n";
                    }
                    else{
                    //    simpleRecord += "; ";
                        priorityRecord += "; ";
                    }

                    //simpleWriter.write(simpleRecord);
                    priorityWriter.write(priorityRecord);
                }
            }

            //simpleWriter.close();
            priorityWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    private void doEditDistanceScaleTest(int[] numberOfTerms, ArrayList<String> terms, String type) {
        try {
            File simpleAverageResultFile = new File(resultPath + "simpleEditDistanceTest" + type + System.currentTimeMillis() + fileEnding);
            File priorityAverageResultFile = new File(resultPath + "priorityEditDistanceTest" + type + System.currentTimeMillis() + fileEnding);
            BufferedWriter simpleWriter = new BufferedWriter(new FileWriter(simpleAverageResultFile));
            BufferedWriter priorityWriter = new BufferedWriter(new FileWriter(priorityAverageResultFile));
            doEditDistanceScalingSearch(numberOfTerms, terms, simpleWriter, priorityWriter, 10);


            simpleWriter.close();
            priorityWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void doEditDistanceScalingSearch(int[] numberOfTerms, ArrayList<String> terms, BufferedWriter simpleWriter, BufferedWriter priorityWriter, int numberOfSuggestions) throws IOException {
        for(int i = 0; i < numberOfTerms.length; i++){
            System.out.println("EditDistanceScaling: " + i);
            for(int j = 0; j < numberOfTerms[i]; j++){
                String term = terms.get(j);
                String modifiedTerm = TermModifier.modifyTerm(i, term);
                searchHandler = new InteractiveSearchHandler(index, numberOfSuggestions, false, i, interfaceControl);
                long simpleTime = doInteractiveSearch(modifiedTerm);
                searchHandler = new InteractiveSearchHandler(index, numberOfSuggestions, true, i, interfaceControl);
                long priorityTime = doInteractiveSearch(modifiedTerm);

                String simpleRecord = "" + simpleTime;
                String priorityRecord = "" + priorityTime;
                if(j == numberOfTerms[i] - 1){
                    simpleRecord += "\n";
                    priorityRecord += "\n";
                }
                else{
                    simpleRecord += ", ";
                    priorityRecord += ", ";
                }

                simpleWriter.write(simpleRecord);
                priorityWriter.write(priorityRecord);
            }
        }
    }

    public void individualCharacterIterationTest(){
        System.out.println("Individual character test");
        File simpleAverageResultFile = new File(resultPath + "simpleAverageIndividualCharacterIterationTest" + System.currentTimeMillis() + fileEnding);
        File priorityAverageResultFile = new File(resultPath + "priorityAverageIndividualCharacterIterationTest" + System.currentTimeMillis() + fileEnding);
        int numberOfIterations = 7;
        int numberOfQueries = 1000;
        int numberOfEdits = 2;

        try {
            BufferedWriter simpleWriter = new BufferedWriter(new FileWriter(simpleAverageResultFile));
            BufferedWriter priorityWriter = new BufferedWriter(new FileWriter(priorityAverageResultFile));

            int numberOfRecivedIndexTerms = 0;
            while(numberOfRecivedIndexTerms < numberOfQueries){
                String candidateTerm = index.getRandomIndexTerm();
                String modifiedTerm = TermModifier.modifyTerm(numberOfEdits, candidateTerm);
                if(modifiedTerm.length() >= numberOfIterations){
                    numberOfRecivedIndexTerms++;

                    searchHandler = new InteractiveSearchHandler(index, 10, false, numberOfEdits, interfaceControl);
                    performIterativeSearchWithBookkeping(numberOfIterations, simpleAverageResultFile, modifiedTerm, simpleWriter);
                    searchHandler = new InteractiveSearchHandler(index, 10, true, numberOfEdits, interfaceControl);
                    performIterativeSearchWithBookkeping(numberOfIterations, priorityAverageResultFile, modifiedTerm, priorityWriter);
                }
            }

            simpleWriter.close();
            priorityWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void performIterativeSearchWithBookkeping(
            int largestTerm,
            File resultFile,
            String testQuery,
            BufferedWriter resultWriter) throws IOException
    {
        ArrayList<Long> results = new ArrayList<Long>();
        for(int i = 0; i < largestTerm; i++){
            String queryString = testQuery.substring(0, i);
            long startTime = System.nanoTime();
            searchHandler.handleUserInputAsync(queryString);
            long endTime = System.nanoTime();
            long timeUsage = endTime - startTime;
            results.add(timeUsage);
        }

        StringBuilder resultRecord = new StringBuilder("");

        for(int i = 0; i < results.size(); i++){
            resultRecord.append(results.get(i));
            if(!(i == results.size() - 1)){
                resultRecord.append("; ");
            }
        }

        resultRecord.append("\n");
        resultWriter.append(resultRecord.toString());
    }

    private long doInteractiveSearch(String term){
        long totalTime = 0;
        for(int i = 1; i <= term.length(); i++){
            String queryString = term.substring(0, i);
            long startTime = System.nanoTime();
            searchHandler.handleUserInputAsync(queryString);
            long endTime = System.nanoTime();
            totalTime += endTime - startTime;
        }

        return totalTime;
    }

    public static void main(String[] args){
        PerformanceTest performanceTest = new PerformanceTest(100000);
        //performanceTest.individualCharacterIterationTest();
        performanceTest.kScalingTest();
        //indexSizeScalingTest();
    }

    public static void indexSizeScalingTest(){
        int stepSize = 10000;
        int maxSize = 100000;

        try {
            File simpleAverageResultFile = new File(resultPath + "simpleIndexSize2" + fileEnding);
            File priorityAverageResultFile = new File(resultPath + "priorityIndexSize2" + fileEnding);
            BufferedWriter simpleWriter = new BufferedWriter(new FileWriter(simpleAverageResultFile));
            BufferedWriter priorityWriter = new BufferedWriter(new FileWriter(priorityAverageResultFile));
            for(int i = 10000; i <= maxSize; i += stepSize){
                PerformanceTest performanceTest = new PerformanceTest(i);
                performanceTest.plainSearchTest(simpleWriter, priorityWriter);
            }

            simpleWriter.close();
            priorityWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private final class DummyInterface implements IUpdateInterfaceControl{

        @Override
        public void addSuggestion(ArrayList<String> suggestionList) {
            //Empty
        }

        @Override
        public void updateWorkingStatus(WorkingStatus workingStatus) {
            //Empty
        }
    }*/
}