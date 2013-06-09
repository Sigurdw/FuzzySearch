package MedlineParser;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.*;
import java.util.Scanner;
import java.util.StringTokenizer;

public class MedlineParser {

    private static final String Path = "D:/medline2004.txt";

    private static final String DocPrefix = "Medline";

    private static final String TargetPath = "D:/Index/";

    public static void main(String[] args) {
        try {
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            Directory indexDirectory = FSDirectory.open(new File("D:/Index/"));
            IndexWriter indexWriter = new IndexWriter(indexDirectory, config);

            File file = new File(Path);
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter("TITLE = ");
            int counter = 0;
            while (scanner.hasNext()){
                scanner.nextLine();
                scanner.nextLine();
                scanner.nextLine();
                scanner.nextLine();
                scanner.skip("TITLE = ");
                String title = scanner.nextLine();
                scanner.skip("ABSTRACT = ");
                String content = scanner.nextLine();
                Document doc = new Document();
                Field titleField = new Field("title", title, Field.Store.NO, Field.Index.ANALYZED);
                Field contentField = new Field("content", content, Field.Store.NO, Field.Index.ANALYZED);
                doc.add(titleField);
                doc.add(contentField);
                indexWriter.addDocument(doc);
                counter++;

                if(counter % 50000 == 0){
                    System.out.println(counter);
                    //System.out.println(title);
                    //System.out.println(content);
                }
            }

            indexWriter.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found.");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in document processing.");
            System.exit(1);
        }
    }

    private static int WriteDoc(int counter, String text, BufferedWriter bf) throws IOException {
        try{
            String fileName = "C:/Medline2004/" + DocPrefix + counter + ".txt";
            File newFile = new File(fileName);
            if(counter % 100 == 0){
                System.out.println(fileName);
            }

            bf = new BufferedWriter(new FileWriter(newFile));
            bf.write(text);
            counter++;
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        finally {
            if(bf != null){
                bf.close();
            }

        }
        return counter;
    }
}
