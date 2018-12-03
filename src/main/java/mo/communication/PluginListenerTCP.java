package mo.communication;

import java.util.EventListener;

public interface PluginListenerTCP extends EventListener{
    public void onMessageReceived(Object obj, PetitionResponse pr);
}
