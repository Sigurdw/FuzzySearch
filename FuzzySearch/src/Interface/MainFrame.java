package Interface;

import DocumentModel.IDocument;
import Index.Index;
import Index.Indexer;
import Index.Crawler;
import Query.InteractiveSearchHandler;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainFrame extends JFrame {

    private static final int width = 800;
    private static final int height = 600;


    public MainFrame() throws Exception{
        setSize(width, height);
        SearchPanel searchPanel = new SearchPanel();
        add(searchPanel);
        setTitle("Fuzzy Search");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String args[]) throws Exception{
        new MainFrame();
    }
}
