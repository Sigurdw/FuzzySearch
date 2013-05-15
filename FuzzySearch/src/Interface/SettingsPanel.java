package Interface;

import Config.IConfigListener;
import Config.SearchConfig;
import DataStructure.IIndexProgressListener;
import DataStructure.Index;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * Author: Sigurd Wien
 * Date: 29.03.13
 * Time: 16:16
 */
public class SettingsPanel extends JPanel {
    private final JFileChooser fileChooser = new JFileChooser();
    private final JProgressBar progressBar = new JProgressBar();
    private final JTextField currentIndexLabel = new JTextField(32);
    private final JTextField neededSuggestionsField = new JTextField(32);
    private final JTextField allowedEditsField = new JTextField(32);
    private final JTextField editDiscountField = new JTextField(32);
    private final JTextField semanticActivatedField = new JTextField();
    private final JTextField separateTermsField = new JTextField();
    private final IConfigListener configListener;

    private SearchConfig currentConfig = SearchConfig.DummyConfig;

    public SettingsPanel(IConfigListener configListener){
        this.configListener = configListener;
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JButton loadIndexButton = new JButton("Load index");
        loadIndexButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayFileChooser();
            }
        });

        add(loadIndexButton);
        add(progressBar);
        add(currentIndexLabel);
        JLabel neededSuggestionsLabel = new JLabel("Needed Suggestions");
        add(neededSuggestionsLabel);
        neededSuggestionsField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateNeededSuggestions();
            }
        });
        add(neededSuggestionsField);

        JLabel allowedEditsLabel = new JLabel("Allowed Edits");
        add(allowedEditsLabel);
        allowedEditsField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAllowedEdits();
            }
        });
        add(allowedEditsField);

        editDiscountField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateEditDiscount();
            }
        });
        JLabel editDiscountLabel = new JLabel("Edit discount");
        add(editDiscountLabel);
        add(editDiscountField);

        JButton toggleSemanticMultiTermsButton = new JButton("Toggle semantic");
        toggleSemanticMultiTermsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSemanticsEnabled();
            }
        });
        add(toggleSemanticMultiTermsButton);
        add(semanticActivatedField);

        JButton toggleSeparateTermsButton = new JButton("Separate terms");
        toggleSeparateTermsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSeparateTermsEnabled();
            }
        });

        add(toggleSeparateTermsButton);
        add(separateTermsField);

        updateView();
        configListener.configUpdated(currentConfig);
    }

    private void updateSeparateTermsEnabled() {
        boolean separateTerms = !currentConfig.isSeparateTermEvaluation();
        currentConfig = currentConfig.updateSeparateTermEvaluation(separateTerms);
        configListener.configUpdated(currentConfig);
        updateView();
    }

    private void updateSemanticsEnabled() {
        boolean semanticsEnabled = !currentConfig.isSemanticEnabled();
        currentConfig = currentConfig.updateConfig(semanticsEnabled);
        configListener.configUpdated(currentConfig);
        updateView();
    }

    private void updateEditDiscount() {
        String allowedEditsString = editDiscountField.getText();
        float editDiscount = -1;

        boolean inputAccepted = true;
        try{
            editDiscount = Float.parseFloat(allowedEditsString);

            if(editDiscount < 0 || editDiscount > 1.0){
                inputAccepted = false;
            }
        }
        catch (NumberFormatException nfe){
            inputAccepted = false;
        }

        if(inputAccepted){
            currentConfig = currentConfig.updateConfig(editDiscount);
            configListener.configUpdated(currentConfig);
        }

        updateView();
    }

    private void updateNeededSuggestions() {
        String neededSuggestionsString = neededSuggestionsField.getText();
        int neededSuggestions = -1;

        boolean inputAccepted = true;
        try{
            neededSuggestions = Integer.parseInt(neededSuggestionsString);

            if(neededSuggestions < 0){
                inputAccepted = false;
            }
        }
        catch (NumberFormatException nfe){
            inputAccepted = false;
        }

        if(inputAccepted){
            currentConfig = currentConfig.updateNeededSuggestionConfig(neededSuggestions);
            configListener.configUpdated(currentConfig);
        }

        updateView();
    }

    private void updateAllowedEdits() {
        String allowedEditsString = allowedEditsField.getText();
        int allowedEdits = -1;

        boolean inputAccepted = true;
        try{
            allowedEdits = Integer.parseInt(allowedEditsString);

            if(allowedEdits < 0){
                inputAccepted = false;
            }
        }
        catch (NumberFormatException nfe){
            inputAccepted = false;
        }

        if(inputAccepted){
            currentConfig = currentConfig.updateConfig(allowedEdits);
            configListener.configUpdated(currentConfig);
        }

        updateView();
    }

    private void displayFileChooser(){
        fileChooser.showOpenDialog(this);
        File currentFile = fileChooser.getSelectedFile();
        if(currentFile != null){
            new IndexReader(currentFile).execute();
        }
    }

    private void updateView()
    {
        String currentFilePath = currentConfig.getCurrentIndex() != null
                ? "Index loaded."
                : "No index loaded.";
        currentIndexLabel.setText(currentFilePath);
        neededSuggestionsField.setText(String.valueOf(currentConfig.getNeededSuggestion()));
        allowedEditsField.setText(String.valueOf(currentConfig.getAllowedEdits()));
        editDiscountField.setText(String.valueOf(currentConfig.getEditDiscount()));
        semanticActivatedField.setText(String.valueOf(currentConfig.isSemanticEnabled()));
        separateTermsField.setText(String.valueOf(currentConfig.isSeparateTermEvaluation()));
    }

    private void updateProgress(int progress){
        progressBar.setValue(progress);
    }

    public static void main(String args[]){
        JFrame frame = new JFrame();
        frame.add(new SettingsPanel(new IConfigListener() {
            @Override
            public void configUpdated(SearchConfig newConfig) {
                System.out.println(newConfig);
            }
        }));
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    private class IndexReader extends SwingWorker<Index, Integer> implements IIndexProgressListener {

        private final File fileToRead;

        public IndexReader(File fileToRead){

            this.fileToRead = fileToRead;
        }

        @Override
        protected Index doInBackground() throws Exception {
            publish(0);
            Index index = Index.read(new DataInputStream(new FileInputStream(fileToRead)), this);
            publish(100);
            return index;
        }

        @Override
        protected void process(List<Integer> progresses){
            final Integer lastProgress = progresses.get(progresses.size() - 1);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    updateProgress(lastProgress);
                }
            });
        }

        @Override
        protected void done(){
            Index index = null;
            try{
                index = get();
            }
            catch (Exception e){
                System.out.println(e.getStackTrace());
                System.exit(1);
            }

            currentConfig = currentConfig.updateConfig(index);
            configListener.configUpdated(currentConfig);
            updateView();
        }

        @Override
        public void setReadProgress(int percentage) {
            publish(percentage);
        }
    }
}
