package Interface;

import javax.swing.*;

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
