package mo.communication.connectionmanagement;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import mo.communication.ClientConnection;
import mo.communication.streaming.visualization.VisualizeStreamingAction;
import mo.core.I18n;
import mo.core.MultimodalObserver;
import mo.core.plugin.Extends;
import mo.core.plugin.Extension;
import mo.core.preferences.AppPreferencesWrapper;
import mo.core.preferences.PreferencesManager;
import mo.core.ui.WizardDialog;
import mo.core.ui.menubar.IMenuBarItemProvider;
import mo.core.plugin.Plugin;
import mo.core.plugin.PluginRegistry;
import mo.core.ui.dockables.DockableElement;
import mo.core.ui.dockables.IDockableElementProvider;
import static mo.core.ui.menubar.MenuItemLocations.UNDER;

public class ConnectionManagement{

    private I18n inter;
    

    public ConnectionManagement() {
        inter = new I18n(ConnectionManagement.class);
        newConnection();
    }

    
    private void newConnection() {
        WizardDialog w = new WizardDialog(
                null, inter.s("NewConnectionWizardPanel.newConnectionWizardTitle"));
        w.addPanel(new NewConnectionWizardPanel(w));
        HashMap<String, Object> result = w.showWizard();
        if (result != null) {
            int port;
            String ipAddress;
            try {
                port = Integer.parseInt(result.get("port").toString());
                ipAddress = result.get("ipAddress").toString();
                boolean isConnected = ClientConnection.getInstance().addServer(ipAddress, port);
                if(isConnected){
                    return;
                }
            } catch (InterruptedException | IOException | NumberFormatException | ClassNotFoundException ex) {}
            
            JOptionPane.showMessageDialog(null, "No se pudo establecer conexión, intente nuevamente");
        }
    }

}
