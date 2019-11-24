package mo.communication;

import mo.communication.chat.ChatWindow;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import mo.communication.notes.NotesWindow;
import mo.communication.ConnectionSender;
import mo.communication.connectionmanagement.ConnectionM;
import mo.communication.notes.RemoteNotesPanel;
import mo.communication.streaming.visualization.VisualizableStreamingConfiguration;
import mo.core.I18n;
import mo.core.plugin.Plugin;
import mo.core.plugin.PluginRegistry;
import mo.core.ui.dockables.DockablesRegistry;

public class ClientConnection implements ConnectionSender, ConnectionListener{
    private RemoteServer server; //servers online
    private String localIP = null; // ip local (client)
    private ChatWindow chat;
    private RemoteNotesPanel notesPanel;
    private ConnectionM dockableNewConnection;
    private I18n inter;

    
    private static ClientConnection connection;
    private ClientConnection(){
        try {
            localIP = InetAddress.getLocalHost().getHostAddress();
            dockableNewConnection = new ConnectionM();
            inter = new I18n(ClientConnection.class);
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    downClient();
                }
            }));
        } catch (UnknownHostException ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static ClientConnection getInstance() throws UnknownHostException{
        if(connection == null)
            connection = new ClientConnection();
        return connection;
    }
    
    public boolean addServer(String ip, int port) throws InterruptedException, IOException, ClassNotFoundException{
        RemoteServer rs = new RemoteServer(ip,port);
        if(rs.connect()){
            server = rs;
            server.listenTCP(); // escucha socket TCP
            server.getCapturePlugins(); // pide plugins
            server.getPorts(); // pide puertos UDP y RTP
            initAndAddComponents(); // chat, notas y cámara/pantalla
            return true;
        }
        return false;
    }
    
    public void downClient(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(server != null){
                    try {
                        //        System.out.println("TODO: descoenctar todo");
                        PetitionResponse petition = new PetitionResponse(Command.END_CONNECTION,null);
                        server.send(petition);
                        server.disconnect();
                        server = null;
                        for(CommunicationConfiguration config: configsPluginsTCP){
                            config.closePlayer();
                            System.out.println("Cerrando: "+config.toString());
                        }
                        configsPluginsTCP.clear();
                        listeners.clear();
                        dockableNewConnection.onMessageReceived(null, Command.END_CONNECTION);
                        DockablesRegistry.getInstance().closeDockableByGroup(localIP);
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SocketException ex) {
                        Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NullPointerException ex){
                        Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();
    }
    
    ArrayList<CommunicationConfiguration> configsPluginsTCP;
    ArrayList<CommunicationProvider1> pluginsTCP;
    String name;
    public void setName(String newName){
        this.name = newName;
    }
    public void initAndAddComponents() throws UnknownHostException{
//        while(name == null){
//            name = JOptionPane.showInputDialog(
//                        inter.s("ClientConnection.nameDialog"), null);
//        }
        //chat = new ChatWindow(name);
        configsPluginsTCP = new ArrayList<>();
        pluginsTCP = new ArrayList<>();
        
        for (Plugin plugin : PluginRegistry.getInstance().getPluginData().getPluginsFor("mo.communication.CommunicationProvider1")) {
            CommunicationProvider1 c = (CommunicationProvider1) plugin.getNewInstance();
            pluginsTCP.add(c);
        }
        
        for(CommunicationProvider1 plugin: pluginsTCP){
            CommunicationConfiguration config = plugin.initNewConfiguration(name);
            configsPluginsTCP.add(config);
            if(config instanceof ConnectionListener){
                ClientConnection.getInstance().subscribeListener((ConnectionListener) config);
            }
            if(config instanceof ConnectionSender){
                ((ConnectionSender) config).subscribeListener(this);
                System.out.println("Se suscribió "+config.toString()+" a Connection");
            }
            config.showPlayer();
        }
        
        //notesPanel = new RemoteNotesPanel();
        //NotesWindow nw = new NotesWindow();
    }

    public RemoteServer getServer() {
        return server;
    }
    
    private ArrayList<ConnectionListener> listeners;
//    public static void subscribeListener(ConnectionListener c){
//        if (listeners == null)
//            listeners = new ArrayList<>();
//        listeners.add(c);
//    }
    
    public ArrayList<ConnectionListener> getListeners(){
        return listeners;
    }

    @Override
    public void subscribeListener(ConnectionListener c) {
        if (listeners == null)
            listeners = new ArrayList<>();
        listeners.add(c);
        /*
        if(c instanceof VisualizableStreamingConfiguration)
            System.out.println("Se ha suscrito uno más de nombre: "+((VisualizableStreamingConfiguration)c).getId());
        if(c instanceof CommunicationConfiguration)
            System.out.println("Se ha suscrito uno más de nombre: "+((CommunicationConfiguration)c).toString());
        */
    }

    @Override
    public void unsubscribeListener(ConnectionListener c) {
        if (listeners == null || listeners.isEmpty() || !listeners.contains(c))
            return;
        listeners.remove(c);
        System.out.println("Se ha suscrito uno más");
    }

    public ChatWindow getChat() {
        return chat;
    }

    public String getLocalIP() {
        return localIP;
    }

    @Override
    public void onMessageReceived(Object obj, PetitionResponse pr) {
        try {
            //server.send(pr);
            server.handlerTCP(pr);
        } catch (UnknownHostException ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void subscribeToConnection(ClientConnection cc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
