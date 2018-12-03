package mo.communication.connectionmanagement;

import mo.core.Utils;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import mo.core.I18n;
import mo.core.ui.WizardDialog;

public class NewConnectionWizardPanel extends JPanel {
    WizardDialog wizard;
    private final JTextField ipField;
    private final JTextField portField;
    private I18n inter;
    
    public NewConnectionWizardPanel(WizardDialog wizard) {
        this.wizard = wizard;
        inter = new I18n(NewConnectionWizardPanel.class);
        super.setName(inter.s("NewConnectionWizardPanel.newConnectionStep"));
        super.setLayout(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        
        JLabel ipAddressLabel = new JLabel(inter.s("NewConnectionWizardPanel.newConnectionIP"));
        ipField = new JTextField();
        ipField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateState();
            }
        });
        
        JLabel portLabel = new JLabel(inter.s("NewConnectionWizardPanel.newConnectionPort"));
        portField = new JTextField();
        portField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateState();
            }
        });
        
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 5, 2, 5);
        c.anchor = GridBagConstraints.LINE_START;
        super.add(ipAddressLabel, c);
        
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(10, 5, 2, 5);
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        super.add(ipField, c);
        
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        c.insets = new Insets(2, 5, 2, 5);
        c.fill = GridBagConstraints.NONE;
        super.add(portLabel, c);
        
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        super.add(portField, c);
        
        c.gridy = 3;
        c.gridx = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 3;
        c.ipadx = 300; //450
        c.ipady = 0; //150
        super.add(new JLabel(""), c);
        
        wizard.setWarningMessage(inter.s("NewConnectionWizardPanel.warningFailConnection"));
    }
    
    public static boolean validateIP(final String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        return ip.matches(PATTERN);
    }
    
    private boolean validatePort(final String port) {
        try {
            if(port == null || port.equals("")) return false;
            return Integer.parseInt(port) < 10000;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private void updateState(){
        if (!validateIP(ipField.getText())){
            wizard.setWarningMessage(inter.s("NewConnectionWizardPanel.warningIP"));
            wizard.nullResult();
            wizard.disableFinish();
        } else if (!validatePort(portField.getText())) {
            wizard.setWarningMessage(inter.s("NewConnectionWizardPanel.warningPort"));
            wizard.nullResult();
            wizard.disableFinish();
        } else {
            wizard.addResult("ipAddress", ipField.getText());
            wizard.addResult("port", portField.getText());
            wizard.setWarningMessage("");
            wizard.enableFinish();
        }
    }
}
