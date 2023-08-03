package de.turtleboi.turtlenet.core;

import de.turtleboi.turtlenet.api.TurtleNet;
import de.turtleboi.turtlenet.api.environment.Environment;
import de.turtleboi.turtlenet.core.util.ResourceUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TurtleCore implements TurtleNet {
    public static final String VERSION = ResourceUtil.getProperty("version.properties", "version");
    private final Logger logger;
    private final Environment environment;
    private final File dataFolder;
    private final File jarFile;

    TurtleCore(@NotNull Logger logger, @NotNull Environment environment, @NotNull File dataFolder, @NotNull File jarFile) {
        this.logger = logger;
        this.environment = environment;
        this.dataFolder = dataFolder;
        this.jarFile = jarFile;
    }

    @Override
    public void shutdown(long timeout, @NotNull TimeUnit unit) throws IOException {
        // TODO
    }

    @Override
    public void shutdownNow() throws IOException {
        // TODO
    }
}
