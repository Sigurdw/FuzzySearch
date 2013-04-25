package Interface;

import Query.QueryWorker;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.util.ArrayList;

public class SearchPanel extends JPanel implements IUpdateInterfaceControl {

    private final JTextField searchField = new JTextField(50);
    private final JTextArea resultArea = new JTextArea(10, 50);
    private final JButton dummyButton = new JButton("Search");

    private final QueryWorker queryWorker;

    public SearchPanel() throws Exception{
        queryWorker = new QueryWorker(this);
        searchField.setEditable(true);
        searchField.addCaretListener( new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                String queryString = searchField.getText();
                handleUserInput(queryString);
            }
        });

        add(searchField);
        resultArea.setEditable(false);
        add(resultArea);
        dummyButton.setForeground(WorkingStatus.getStatusColor(WorkingStatus.IterationExhausted));
        add(dummyButton);

        queryWorker.startWorker();
    }

    private void handleUserInput(String queryString){
        queryWorker.updateQueryString(queryString);
    }

    @Override
    public void updateSuggestionList(final ArrayList<String> suggestionList) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                resultArea.setText("");
                for(String suggestion : suggestionList){
                    resultArea.append(suggestion + "\n");
                }
            }
        });
    }

    @Override
    public void updateWorkingStatus(final WorkingStatus workingStatus) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dummyButton.setForeground(WorkingStatus.getStatusColor(workingStatus));
            }
        });
    }
}
