package mo.communication;

public interface PluginSenderTCP {
    public void subscribeListener(PluginListenerTCP pl);
    public void unsubscribeListener(PluginListenerTCP pl);
}
