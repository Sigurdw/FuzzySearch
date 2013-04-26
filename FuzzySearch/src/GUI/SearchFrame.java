package GUI;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Sigurd Wien
 * Date: 04.04.13
 * Time: 08:46
 */
public class SearchFrame extends JFrame {

    public SearchFrame(){
        SearchPanel searchPanel = new SearchPanel();
        add(searchPanel, BorderLayout.EAST);
        add(new SettingsPanel(searchPanel), BorderLayout.WEST);
        setSize(800, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String args[]){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame mainFrame = new SearchFrame();
                mainFrame.setVisible(true);
            }
        });
    }
}
