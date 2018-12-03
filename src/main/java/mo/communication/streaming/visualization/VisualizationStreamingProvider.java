package mo.communication.streaming.visualization;

import java.io.File;
import java.util.List;
import mo.communication.streaming.capture.CaptureConfig;
import mo.core.plugin.ExtensionPoint;
import mo.organization.Configuration;

@ExtensionPoint
public interface VisualizationStreamingProvider {
    String getName();
    List<String> getCompatibleCreators();
    Configuration initNewStreamingConfiguration(CaptureConfig cc);
    List<Configuration> getConfigurations();
}