package mo.communication.streaming.visualization;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import mo.core.I18n;
import mo.core.plugin.Plugin;
import mo.core.plugin.PluginRegistry;

public class VisualizationStreamingStage {
    
    private List<VisualizationStreamingProvider> plugins;
    //private List<StageAction> actions;
    private List<VisualizeStreamingAction> actions;
    private I18n i18n;
    
    private static final String CODENAME = "streaming-visualization";
    
    private static final Logger logger
            = Logger.getLogger(VisualizationStreamingStage.class.getName());

    public VisualizationStreamingStage() throws UnknownHostException {
        i18n = new I18n(VisualizationStreamingStage.class);
        
        plugins = new ArrayList<>();
        actions = new ArrayList<>();
        
        for (Plugin plugin : PluginRegistry.getInstance().getPluginData().getPluginsFor("mo.communication.streaming.visualization.VisualizationStreamingProvider")) {
            VisualizationStreamingProvider p = (VisualizationStreamingProvider) plugin.getNewInstance();
            plugins.add(p);
        }
        
        VisualizeStreamingAction va = new VisualizeStreamingAction();
        actions.add(va);
        //va.init2();
    }

    public String getCodeName() {
        return CODENAME;
    }

    public String getName() {
        return i18n.s("VisualizationStreamingStage.streaming");
    }

    public List<VisualizationStreamingProvider> getPlugins() {
        return plugins;
    }

    public File toFile(File parent) {
        return null;
    }


    public List<VisualizeStreamingAction> getActions() {
        return actions;
    }
}
