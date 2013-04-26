package GUI;

import Config.IConfigListener;
import Config.SearchConfig;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Sigurd Wien
 * Date: 29.03.13
 * Time: 15:43
 */
public class SearchPanel extends JPanel implements IConfigListener {
    private final JLabel searchLabel = new JLabel("Write your query in the search box.");
    private final JTextField searchField = new JTextField(30);
    private final JButton searchButton = new JButton("Search");
    private final JTextArea searchResultArea = new JTextArea();

    public SearchPanel()
    {
        setLayout(new BorderLayout());
        add(searchLabel, BorderLayout.NORTH);
        searchField.setEditable(true);
        searchField.setDragEnabled(false);
        add(searchField, BorderLayout.EAST);
        String searchButtonText = searchButton.getText();
        FontMetrics fontMetrics = getFontMetrics(getFont());
        int width = fontMetrics.stringWidth(searchButtonText);
        int height = fontMetrics.getHeight();
        //searchButton.setPreferredSize(new Dimension(60, 30));
        searchResultArea.setPreferredSize(new Dimension(200, 520));

        add(searchButton, BorderLayout.WEST);
        add(searchResultArea, BorderLayout.SOUTH);
    }

    public static void main(String args[]){
        JFrame frame = new JFrame();
        frame.add(new SearchPanel());
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    @Override
    public void configUpdated(SearchConfig newConfig) {
        System.out.println(newConfig);
    }
}
