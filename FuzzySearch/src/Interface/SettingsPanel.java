package Interface;

import Config.IConfigListener;
import Config.SearchConfig;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Author: Sigurd Wien
 * Date: 29.03.13
 * Time: 16:16
 */
public class SettingsPanel extends JPanel {
    private final JFileChooser fileChooser = new JFileChooser();
    private final JTextField currentIndexLabel = new JTextField(32);
    private final JTextField allowedEditsField = new JTextField(32);
    private final JTextField editDiscountField = new JTextField(32);
    private final JTextField semanticActivatedField = new JTextField();
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
        add(currentIndexLabel);
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

        updateView();
        configListener.configUpdated(currentConfig);
    }

    private void updateSemanticsEnabled() {
        boolean semanticsEnabled = !currentConfig.isSemanticEnabled();
        currentConfig = currentConfig.updateConfig(semanticsEnabled);
        configListener.configUpdated(currentConfig);
        updateView();
    }

    private void updateEditDiscount() {
        String allowedEditsString = editDiscountField.getText();
        double editDiscount = -1;

        boolean inputAccepted = true;
        try{
            editDiscount = Double.parseDouble(allowedEditsString);

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
        currentConfig = currentConfig.updateConfig(fileChooser.getSelectedFile());
        configListener.configUpdated(currentConfig);
        updateView();
    }

    private void updateView()
    {
        String currentFilePath = currentConfig.getCurrentIndex() != null
                ? currentConfig.getCurrentIndex().getPath()
                : "No index selected.";
        currentIndexLabel.setText(currentFilePath);
        allowedEditsField.setText(String.valueOf(currentConfig.getAllowedEdits()));
        editDiscountField.setText(String.valueOf(currentConfig.getEditDiscount()));
        semanticActivatedField.setText(String.valueOf(currentConfig.isSemanticEnabled()));
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
}
