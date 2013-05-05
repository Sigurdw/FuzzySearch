package Interface;

import Config.IConfigListener;
import Config.SearchConfig;
import Query.QueryWorker;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;

public class SearchPanel extends JPanel implements IUpdateInterfaceControl, IConfigListener {
    private final JLabel searchLabel = new JLabel("Write your query in the search box.");
    private final JTextField searchField = new JTextField(30);
    private final JButton searchButton = new JButton("Search");
    private final JTextArea searchResultArea = new JTextArea();

    private final QueryWorker queryWorker;

    public SearchPanel() throws Exception{
        setLayout(new BorderLayout());
        add(searchLabel, BorderLayout.NORTH);
        searchField.setEditable(true);
        searchField.addCaretListener( new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                String queryString = searchField.getText();
                handleUserInput(queryString);
            }
        });
        add(searchField, BorderLayout.EAST);
        searchResultArea.setPreferredSize(new Dimension(200, 520));

        add(searchButton, BorderLayout.WEST);
        add(searchResultArea, BorderLayout.SOUTH);

        queryWorker = new QueryWorker(this);


        searchButton.setForeground(WorkingStatus.getStatusColor(WorkingStatus.IterationExhausted));

        queryWorker.startWorker();
    }

    private void handleUserInput(String queryString){
        queryWorker.updateQueryString(queryString);
    }

    @Override
    public void addSuggestion(final String suggestion) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                searchResultArea.append(suggestion);
            }
        });
    }

    @Override
    public void clearSuggestions() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                searchResultArea.setText("");
            }
        });
    }

    @Override
    public void updateWorkingStatus(final WorkingStatus workingStatus) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                searchButton.setForeground(WorkingStatus.getStatusColor(workingStatus));
            }
        });
    }

    @Override
    public void configUpdated(SearchConfig newConfig) {
        queryWorker.initiateConfigUpdate(newConfig);
    }
}
