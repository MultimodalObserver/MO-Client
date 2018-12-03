package mo.communication.connectionmanagement;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import mo.communication.ClientConnection;
import mo.communication.Command;
import mo.communication.ConnectionListener;
import mo.communication.PetitionResponse;
import mo.core.I18n;
import mo.core.ui.WizardDialog;

public class NewConnectionPanel extends JPanel{
    private final JButton connectButton;
    private final JTextField ipField;
    private final JTextField portField;
    private final JTextField nameField;
    private I18n inter;
    private boolean isConnected;
    public NewConnectionPanel(){
        inter = new I18n(NewConnectionPanel.class);
        
        connectButton = new JButton(inter.s("NewConnectionPanel.connectButton"));
        connectButton.setEnabled(false);
        connectButton.setPreferredSize( new Dimension( 200, 35 ) );
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isConnected)
                    disconnect();
                else
                    connect();
            }
        });
        
        
        
        setLayout(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        
        
        
        JLabel ipAddressLabel = new JLabel(inter.s("NewConnectionPanel.newConnectionIP"));
        ipField = new JTextField();
        ipField.setHorizontalAlignment(JTextField.CENTER);
        ipField.setPreferredSize( new Dimension( 200, 35 ) );
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
        
       
        
        ipField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER && connectButton.isEnabled())
                    updateState();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER && connectButton.isEnabled())
                    connectButton.doClick();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    updateState();
            }
        
        });
        
        JLabel portLabel = new JLabel(inter.s("NewConnectionPanel.newConnectionPort"));
        portField = new JTextField();
        portField.setHorizontalAlignment(JTextField.CENTER);
        portField.setPreferredSize( new Dimension( 200, 35 ) );
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
        
        portField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER && connectButton.isEnabled())
                    updateState();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER && connectButton.isEnabled())
                    connectButton.doClick();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    updateState();
            }
        
        });
        
        
        JLabel nameLabel = new JLabel(inter.s("NewConnectionPanel.Username"));
        nameField = new JTextField();
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setPreferredSize( new Dimension( 200, 35 ) );
        int randomID = (int) (Math.random()*(1+100001)+1);
        nameField.setText("Researcher"+String.valueOf(randomID));
        nameField.getDocument().addDocumentListener(new DocumentListener() {
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
        
        nameField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER && connectButton.isEnabled())
                    updateState();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER && connectButton.isEnabled())
                    connectButton.doClick();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
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
        
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0;
        c.insets = new Insets(2, 5, 2, 5);
        c.fill = GridBagConstraints.NONE;
        super.add(nameLabel, c);
        
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        super.add(nameField, c);
        
        c.gridx = 1;
        c.gridy = 3;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        super.add(connectButton, c);
        
        c.gridy = 4;
        c.gridx = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 4;
        c.ipadx = 300; //450
        c.ipady = 0; //150
        super.add(new JLabel(""), c);
        
        setVisible(true);
    }
    
    private void connect(){
        int port;
        String ipAddress;
        String name;
        try {
            port = Integer.parseInt(portField.getText().toString());
            ipAddress = ipField.getText().toString();
            name = nameField.getText().toString();
            
            if(ClientConnection.getInstance().addServer(ipAddress, port)){
                ipField.setEnabled(false);
                portField.setEnabled(false);
                nameField.setEnabled(false);
                connectButton.setText(inter.s("NewConnectionPanel.disconnectButton"));
                isConnected = true;
                ClientConnection.getInstance().setName(name);
                return;
            }
        } catch (InterruptedException | IOException | NumberFormatException | ClassNotFoundException ex) {}

        JOptionPane.showMessageDialog(null, inter.s("NewConnectionPanel.errorPopup"));
    }
    
    private void disconnect(){
        try {
            ClientConnection.getInstance().downClient();
            ipField.setEnabled(true);
            portField.setEnabled(true);
            nameField.setEnabled(true);
            connectButton.setText(inter.s("NewConnectionPanel.connectButton"));
            isConnected = false;
        } catch (UnknownHostException ex) {}
    }
    
    
    
    
    
    
    
    
    public static boolean validateIP(final String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        return ip.matches(PATTERN);
    }
    
    private boolean validatePort(final String port) {
        try {
            if(port == null || port.equals("")) return false;
            return Integer.parseInt(port) > 1001 && Integer.parseInt(port) < 10000;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private void updateState(){
        if (!validateIP(ipField.getText())){
//            wizard.setWarningMessage(inter.s("NewConnectionWizardPanel.warningIP"));
//            wizard.nullResult();
//            wizard.disableFinish();
            connectButton.setEnabled(false);
        } else if (!validatePort(portField.getText())) {
//            wizard.setWarningMessage(inter.s("NewConnectionWizardPanel.warningPort"));
//            wizard.nullResult();
//            wizard.disableFinish();
            connectButton.setEnabled(false);
        } else if(nameField.getText().equals(null) || nameField.getText().equals("")){
            connectButton.setEnabled(false);
        } else {
//            wizard.addResult("ipAddress", ipField.getText());
//            wizard.addResult("port", portField.getText());
//            wizard.setWarningMessage("");
//            wizard.enableFinish();
            connectButton.setEnabled(true);
        }
    }

    public void onMessageReceived(Object obj, String msg) {
        if(msg.equals(Command.END_CONNECTION) && connectButton != null){
            connectButton.doClick();
        }
    }
}
