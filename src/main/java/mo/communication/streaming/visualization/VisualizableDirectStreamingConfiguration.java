package mo.communication.streaming.visualization;

public interface VisualizableDirectStreamingConfiguration extends VisualizableStreamingConfiguration{
    void setIP(String ip);
    void setPort(int port);
}
