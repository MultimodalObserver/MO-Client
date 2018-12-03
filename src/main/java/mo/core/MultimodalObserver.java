package mo.core;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mo.communication.ClientConnection;
import mo.communication.connectionmanagement.ConnectionM;
import mo.communication.connectionmanagement.ConnectionManagement;
import static mo.core.Language.loadLocale;
import mo.core.plugin.PluginRegistry;

public class MultimodalObserver {
    public static final String APP_PREFERENCES_FILE = 
            Utils.getBaseFolder()+"/preferences.xml";
    
    private void nonStaticMain(String args[]){
        loadLocale();
        
        PluginRegistry.getInstance();

        MainWindow window = new MainWindow();
        MainPresenter presenter = new MainPresenter(window);
        presenter.start();
        
        try {
            //        new ConnectionM();
            ClientConnection.getInstance();
        } catch (UnknownHostException ex) {
            Logger.getLogger(MultimodalObserver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String args[]){

        Logger l = Logger.getLogger("");
        l.setLevel(Level.INFO);
        l.getHandlers()[0].setLevel(Level.INFO);
        
        MultimodalObserver app = new MultimodalObserver();
        app.nonStaticMain(args);
    }
}
