package mo.communication.streaming.visualization;

import java.io.File;
import java.util.List;
import mo.organization.Configuration;

public interface VisualizableStreamingConfiguration extends Configuration {
    List<String> getCompatibleCreators();
    PlayableStreaming getPlayer();
}
