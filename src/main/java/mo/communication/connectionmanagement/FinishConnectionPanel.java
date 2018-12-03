package mo.communication.connectionmanagement;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

public class FinishConnectionPanel extends JPanel{
    private JButton disconnectButton = new JButton("CHAO");
    public FinishConnectionPanel(){
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disconnect();
            }
        });
        add(disconnectButton);
        setVisible(false);
    }
    
    private void disconnect(){
        setVisible(false);
        
    }
}
