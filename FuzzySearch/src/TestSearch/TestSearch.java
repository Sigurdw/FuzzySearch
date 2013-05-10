package TestSearch;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class TestSearch {
    private static final String indexPath = "C:/Index/";

    private static final String textCollextion = "C:/Medline2004";

    public static void main(String[] args) throws Exception{
        try{
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            Directory indexDirectory = FSDirectory.open(new File(indexPath));
            IndexWriter indexWriter = new IndexWriter(indexDirectory, config);
            for(int i = 0; i < 10000; i++){
                File document = new File(textCollextion + "/medline" + i + ".txt");
                FileInputStream fileInputStream = new FileInputStream(document);
                Scanner scanner = new Scanner(fileInputStream);
                //new BufferedReader(new InputStreamReader(fileInputStream)
                Field contentField = new Field("content", scanner.nextLine(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES);
                Document doc = new Document();
                doc.add(contentField);
                Field pathField = new Field("url", document.getAbsolutePath(), Field.Store.YES, Field.Index.NO);
                doc.add(pathField);
                Field titleField = new Field("title", document.getCanonicalPath(), Field.Store.YES, Field.Index.NO);
                doc.add(titleField);

                indexWriter.addDocument(doc);
            }

            indexWriter.close();
        }
        catch(Exception e){

        }
    }
}
