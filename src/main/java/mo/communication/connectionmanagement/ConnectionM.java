package mo.communication.connectionmanagement;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import mo.communication.ClientConnection;
import mo.communication.ConnectionListener;
import mo.communication.PetitionResponse;
import mo.core.I18n;
import mo.core.ui.WizardDialog;
import mo.core.ui.dockables.DockableElement;
import mo.core.ui.dockables.DockablesRegistry;

public class ConnectionM{
    private I18n inter;
    private NewConnectionPanel nc;

    public ConnectionM() {
        System.out.println("ENTRO en connectionM");
        inter = new I18n(ConnectionM.class);
        addDockable();
    }
    
    private void addDockable(){
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        panel.add((nc=new NewConnectionPanel()), c);
        
        SwingUtilities.invokeLater(() -> {
            try {
                DockableElement e = new DockableElement();
                e.add(panel);
                DockablesRegistry.getInstance().addAppWideDockable(e);
            } catch (Exception ex) {}
        });
    }

    public void onMessageReceived(Object obj, String msg) {
        nc.onMessageReceived(obj, msg);
    }
}
