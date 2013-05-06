package Interface;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Sigurd Wien
 * Date: 04.04.13
 * Time: 08:46
 */
public class SearchFrame extends JFrame {

    public SearchFrame() throws Exception{
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
                try{
                    JFrame mainFrame = new SearchFrame();
                    mainFrame.setVisible(true);
                }
                catch (Exception e){
                    System.exit(1);
                }
            }
        });
    }
}
