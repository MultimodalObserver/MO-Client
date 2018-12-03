package mo.communication;

import java.io.File;
import mo.organization.Configuration;
import mo.organization.Participant;

public interface StreamableConfiguration extends Configuration {
    /**
     *
     * @param stageFolder
     * @param org
     * @param p
     */
    void setupStreaming();

    /**
     *
     */
    void startStreaming();

    /**
     *
     */
    void pauseStreaming();

    /**
     *
     */
    void resumeStreaming();

    /**
     *
     */
    void stopStreaming();
}
