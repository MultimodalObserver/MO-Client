package mo.communication.streaming.visualization;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mo.communication.ClientConnection;
import mo.communication.ConnectionListener;
import mo.communication.ConnectionSender;
import mo.communication.streaming.capture.CaptureConfig;
import mo.core.plugin.Plugin;
import mo.core.plugin.PluginRegistry;
import mo.core.ui.dockables.DockablesRegistry;
import mo.organization.Configuration;

public class VisualizeStreamingAction {

    public static void main(String[] args) {
        
    }
    
    public String getName() {
        return "Streaming";
    }
    private List<VisualizationStreamingProvider> plugins;
    private List<VisualizationDirectStreamingProvider> directPlugins;
    
    public void initCapturePluginsStreaming(
            HashMap<String,CaptureConfig> capturePlugins) throws UnknownHostException {
        /*
        detecta plugins
        */
        plugins = new ArrayList<>();
        for (Plugin plugin : PluginRegistry.getInstance().getPluginData().getPluginsFor("mo.communication.streaming.visualization.VisualizationStreamingProvider")) {
            VisualizationStreamingProvider p = (VisualizationStreamingProvider) plugin.getNewInstance();
            plugins.add(p);
        }
        
        
        
        /*
        inicializa configuraciones de acuerdo a cada plugin
        */
        //ArrayList<Configuration> configs = new ArrayList<>();
        System.out.println("ESTE METODO INICIO CON:\n"+capturePlugins);
        ArrayList<VisualizableStreamingConfiguration> configs = new ArrayList<>();
        for (VisualizationStreamingProvider plugin : getPlugins()) {
            capturePlugins.forEach((k,v)->{
                //System.out.println(plugin.getCompatibleCreators().contains(v.getCreator()));
                List<String> list = plugin.getCompatibleCreators();
                System.out.println("ESTA ES LA LISTA"+list);
                //if(plugin.getCompatibleCreators().contains(v.getCreator())){
                if(list.contains(v.getCreator())){
                    System.out.println("ENTRO AL IF");
                    Configuration c = plugin.initNewStreamingConfiguration(v);
                    configs.add((VisualizableStreamingConfiguration)c);
                    try {
                        if(c instanceof ConnectionListener){
                            //((ConnectionListener)c).subscribeToConnection(ClientConnection.getInstance());
                            ClientConnection.getInstance().subscribeListener((ConnectionListener) c);
                        }
                        if(c instanceof ConnectionSender){
                            ((ConnectionSender)c).subscribeListener(ClientConnection.getInstance());
                        }
                    } catch (UnknownHostException ex) {}
                }
            });
        }
        
        /*
        
        
        */



        
        //List<VisualizableStreamingConfiguration> configurations;
        //VisualizationStreamingDialog2 d = new VisualizationStreamingDialog2(configs, organization.getLocation());
        //boolean accept = d.show();
        VisualizationStreamingPlayer p = new VisualizationStreamingPlayer(configs, false);
        if(ClientConnection.getInstance().getServer() != null){
            DockablesRegistry.getInstance()
                .addDockableInProjectGroup(
                        ClientConnection.getInstance().getServer().getIp(),
                        p.getDockable());
        }
        
        p.play();
        System.out.println("TERMINO EL METODO CON "+configs);
    }
    
    public void initDirectStreaming(HashMap<String,Object> directStreamingPluginsConfigs, String ip){
        directPlugins = new ArrayList<>();
        ArrayList<VisualizableStreamingConfiguration> configs = new ArrayList<>();
        
        for (Plugin plugin : PluginRegistry.getInstance().getPluginData().getPluginsFor("mo.communication.streaming.visualization.VisualizationDirectStreamingProvider")) {
            VisualizationDirectStreamingProvider p = (VisualizationDirectStreamingProvider) plugin.getNewInstance();
            directPlugins.add(p);
        }
        
        System.out.println("HAY "+directPlugins.size()+" plugins directos");
        for (VisualizationDirectStreamingProvider plugin : getDirectPlugins()) {
            directStreamingPluginsConfigs.forEach((k,v)->{
                if(getEnd(k).equals("PORT")){
                    if(plugin.getCompatibleCreators().contains(removeEnd(k))){
                        int port = Integer.parseInt(String.valueOf(v));
                        Configuration c = plugin.initNewDirectStreamingConfiguration(ip,port);
                        configs.add((VisualizableDirectStreamingConfiguration)c);
                        if(c instanceof ConnectionListener){
                            try {
                                ClientConnection.getInstance().subscribeListener((ConnectionListener) c);
                            } catch (UnknownHostException ex) {
                                Logger.getLogger(VisualizeStreamingAction.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if(c instanceof ConnectionSender){
                            try {
                                ((ConnectionSender)c).subscribeListener(ClientConnection.getInstance());
                            } catch (UnknownHostException ex) {
                                Logger.getLogger(VisualizeStreamingAction.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        System.out.println("HA ENCONTRADO UNA CONFIGURACION DE STREAMING DIRECTO");
                    }
                }
            });
                
        }
        
        
        VisualizationStreamingPlayer p = new VisualizationStreamingPlayer(configs,true);
        p.play();
        
    }
    
    public String removeEnd(String in) {
        String firstWords = in.substring(0, in.lastIndexOf(" "));
        return firstWords;
    }
    
    public String getEnd(String in) {
        String firstWords = in.substring(in.lastIndexOf(" ")+1);
        return firstWords;
    }
    
    public void init2() {
        try {
            VisualizationStreamingPlayer p = new VisualizationStreamingPlayer();
            //DockablesRegistry.getInstance().addAppWideDockable(p.getDockable());
            DockablesRegistry.getInstance()
                    .addDockableInProjectGroup(ClientConnection.getInstance().getServer().getIp(),
                            p.getDockable());
        } catch (UnknownHostException ex) {}
    }
    public List<VisualizationStreamingProvider> getPlugins() {
        return plugins;
    }
    
    public List<VisualizationDirectStreamingProvider> getDirectPlugins() {
        return directPlugins;
    }
}
