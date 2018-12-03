/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.communication.streaming.visualization;

import mo.communication.streaming.capture.CaptureConfig;
import mo.core.plugin.ExtensionPoint;
import mo.organization.Configuration;

@ExtensionPoint
public interface VisualizationDirectStreamingProvider extends VisualizationStreamingProvider{
    Configuration initNewDirectStreamingConfiguration(String ip, int port);
}
