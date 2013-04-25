package Index;

import DocumentModel.Document;
import DocumentModel.IDocument;

import java.io.File;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Sigurd
 * Date: 23.09.12
 * Time: 18:55
 * To change this template use File | Settings | File Templates.
 */
public class Crawler {
    private File directory;

    public Crawler(String directoryPath){
        directory = new File(directoryPath);
        ensurePathToDirectory();
    }

    private void ensurePathToDirectory() {
        if(!directory.isDirectory()){
            System.out.println("Path" + directory.getPath() + "is not a directory.");
            System.exit(1);
        }
    }

    public ArrayList<IDocument> crawlDirectory(){
        ArrayList<IDocument> documents = new ArrayList<IDocument>();
        for(File document : directory.listFiles()){
            if(document.isFile()){
                System.out.println("Processing document: " + document.getPath());
                documents.add(new Document(document));
            }
            else{
                System.out.println("Not a file, ignoring: " + document.getPath());
            }
        }

        return documents;
    }
}
