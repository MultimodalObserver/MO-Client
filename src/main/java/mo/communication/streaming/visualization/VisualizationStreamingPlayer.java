package mo.communication.streaming.visualization;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mo.core.ui.dockables.DockableElement;

public class VisualizationStreamingPlayer {

    private boolean isPlaying = false;
    private boolean stopped = false;
    private boolean isDirectStreaming = false;

    private Thread playerThread;

    private final List<VisualizableStreamingConfiguration> configs;
    private final List<VisualizableStreamingConfiguration> directConfigs;

    private final PlayerControlsPanel panel;
    private final DockableElement dockable;

    private static final Logger logger = Logger.getLogger(VisualizationStreamingPlayer.class.getName());

    //private byte STOPPED=0, PLAYING=1, PAUSED=2;

    public VisualizationStreamingPlayer(List<VisualizableStreamingConfiguration> configurations, boolean direct) {
        this.isDirectStreaming = direct;
        if(direct){
            configs = null;
            directConfigs = configurations;
            panel = null;

            dockable = null;
        }else{
            configs = configurations;
            directConfigs = null;
            panel = new PlayerControlsPanel(this);

            dockable = new DockableElement();
            dockable.add(panel.getPanel());
            dockable.setTitleText("Player Controls");
        }
       
    }

    
    public VisualizationStreamingPlayer() {
        configs = null;
        directConfigs = null;

        panel = new PlayerControlsPanel(this);

        dockable = new DockableElement();
        dockable.add(panel.getPanel());
        dockable.setTitleText("Player Controls");
    }


    public void pause() {
        playerThread.interrupt();
        isPlaying = false;
    }


    public void play() {
        List<VisualizableStreamingConfiguration> auxConfigs;
        if(isDirectStreaming)
            auxConfigs = directConfigs;
        else
            auxConfigs = configs;

        playerThread = new Thread(() -> {
            isPlaying = true;
                for (VisualizableStreamingConfiguration config : auxConfigs) {
                    //config.getPlayer().play();
                    if(config instanceof VisualizableDirectStreamingConfiguration)
                        ((VisualizableDirectStreamingConfiguration)config).getPlayer().play();
                    else config.getPlayer();
                }
        });
        playerThread.start();
    }
    
    private void sleep(long loopStart) {
        long loopEnd = System.nanoTime();
        long loopTime = loopEnd - loopStart;
        long timeToWait = 1000000 - loopTime;
        if (timeToWait > 0) {
            try {
                Thread.sleep(0, (int) timeToWait);
            } catch (InterruptedException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public DockableElement getDockable() {
        return dockable;
    }

}
