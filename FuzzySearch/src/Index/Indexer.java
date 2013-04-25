package Index;

import DataStructure.Trie;
import DocumentModel.IDocument;
import DocumentModel.MedlineDocument;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

import java.io.*;
import java.util.*;

public class Indexer {

    public static int NumberOfDocuments = 0;
    private int suggestionCacheSize;
    private boolean hasSortedIndex;

    private static final String Dochead = "#NEW RECORDPMID";

    private static final String DocPrefix = "Medline";

    public Indexer(int suggestionCacheSize, boolean hasSortedIndex){
        this.suggestionCacheSize = suggestionCacheSize;
        this.hasSortedIndex = hasSortedIndex;
    }

    public Index indexDocuments(ArrayList<IDocument> documents){
        NumberOfDocuments = documents.size();
        Trie<IDocument> indexImplementation = new Trie<IDocument>(suggestionCacheSize, hasSortedIndex);
        HashSet<String> indexTermSet = new HashSet<String>();
        for(IDocument document : documents){
            List<String> tokens = document.getTokens();
            List<String> indexTerms = getIndexTerms(tokens);
            for(String indexTerm : indexTerms){
                indexImplementation.addKeyDataPair(indexTerm, document);
                indexTermSet.add(indexTerm);
            }
        }

        indexImplementation.sortData();
        System.out.println(
                "Successfully indexed " + indexImplementation.getNumberOfEntries() + " different index terms.");
        return new Index(indexImplementation, indexTermSet);
    }

    public Index indexDocumentBlob(File blobFile, int numberOfDocuments) throws IOException, FileNotFoundException{
        Trie<IDocument> indexImplementation = new Trie<IDocument>(suggestionCacheSize, hasSortedIndex);
        HashSet<String> indexTermSet = new HashSet<String>();

        try {
            Scanner scanner = new Scanner(blobFile);
            scanner.useDelimiter("#NEW RECORD\n");
            int counter = 0;
            while (scanner.hasNext()){

                String document = scanner.next();

                Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);


                if(!document.equals("\n")){
                    StringTokenizer st = new StringTokenizer(document, "\n");
                    assert st.countTokens() == 5;

                    String pmid = st.nextToken();
                    String pubdate = st.nextToken();
                    String title = st.nextToken().substring(8);
                    String text = st.nextToken().substring(11);

                    counter++;
                    MedlineDocument medlineDocument = new MedlineDocument(title, title);

                    StringTokenizer documentTokens = new StringTokenizer(text, " ");
                    indexTokens(indexImplementation, documentTokens, medlineDocument, indexTermSet);
                    StringTokenizer titleTokens = new StringTokenizer(title, " ");
                    indexTokens(indexImplementation, titleTokens, medlineDocument, indexTermSet);
                    if(counter % 10000 == 0){
                        System.out.println("Indexed " + counter);
                    }
                }

                if(counter >= numberOfDocuments){
                    break;
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found.");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in document processing.");
            System.exit(1);
        }

        System.out.println("Sorting data.");
        indexImplementation.sortData();
        System.out.println(
                "Successfully indexed " + indexImplementation.getNumberOfEntries() + " different index terms.");

        return new Index(indexImplementation, indexTermSet);
    }

    private void indexTokens(Trie<IDocument> index, StringTokenizer tokens, IDocument document, HashSet<String> indexTermSet){
        while(tokens.hasMoreTokens()){
            String indexTerm = tokens.nextToken();
            indexTerm = indexTerm.toLowerCase();
            indexTerm = removePrefixSign(indexTerm);
            if(indexTerm.length() > 0){
                indexTerm = removeSigns(indexTerm);
            }

            if(indexTerm.length() > 0){
                index.addKeyDataPair(indexTerm, document);
                indexTermSet.add(indexTerm);
            }
        }
    }

    private List<String> getIndexTerms(List<String> tokens) {
        ArrayList<String> indexTerms = new ArrayList<String>();
        for(String token : tokens){
            String indexTerm = token.toLowerCase();
            indexTerm = removePrefixSign(indexTerm);

            if(indexTerm.length() > 0){
                indexTerm = removeSigns(indexTerm);
            }

            if(indexTerm.length() > 0){
                indexTerms.add(indexTerm);
            }
        }

        return indexTerms;
    }

    private String removePrefixSign(String indexTerm){
        char firstCharacter = indexTerm.charAt(0);
        if(!Character.isLetterOrDigit(firstCharacter)){
            return indexTerm.substring(1, indexTerm.length());
        }

        return indexTerm;
    }

    private String removeSigns(String indexTerm) {
        char lastCharacter = indexTerm.charAt(indexTerm.length() - 1);
        if(!Character.isLetterOrDigit(lastCharacter)){
            return indexTerm.substring(0, indexTerm.length() - 1);
        }

        return indexTerm;
    }
}
