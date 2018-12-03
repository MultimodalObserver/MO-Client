package mo.communication;

import java.util.EventListener;

public interface ConnectionListener extends EventListener{
    public void onMessageReceived(Object obj, PetitionResponse pr);
    //public void subscribeToConnection(ClientConnection cc);
}