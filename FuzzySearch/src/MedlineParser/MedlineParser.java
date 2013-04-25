package MedlineParser;

import java.io.*;
import java.util.Scanner;
import java.util.StringTokenizer;

public class MedlineParser {

    private static final String Path = "C:/TextCollection/medline2004.txt";

    private static final String Dochead = "#NEW RECORDPMID";

    private static final String DocPrefix = "Medline";

    public static void main(String[] args) {
        try {
            File file = new File(Path);
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter("#NEW RECORD\n");
            int counter = 0;
            while (scanner.hasNext()){

                String document = scanner.next();

                if(!document.equals("\n")){
                    StringTokenizer st = new StringTokenizer(document, "\n");
                    assert st.countTokens() == 5;

                    String pmid = st.nextToken();
                    String pubdate = st.nextToken();
                    String title = st.nextToken();
                    String text = st.nextToken().substring(11);

                    BufferedWriter bf = null;
                    if(!text.startsWith("[EMPTY ABSTRACT]")){
                        counter = WriteDoc(counter, text, bf);
                    }
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
