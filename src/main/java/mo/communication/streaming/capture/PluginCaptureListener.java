package mo.communication.streaming.capture;

import java.util.EventListener;

public interface PluginCaptureListener  extends EventListener{
    void onDataReceived(Object obj,CaptureEvent e);
}
