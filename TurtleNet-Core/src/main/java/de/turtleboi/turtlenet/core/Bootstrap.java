package de.turtleboi.turtlenet.core;

import de.turtleboi.turtlenet.api.environment.Environment;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public final class Bootstrap {
    private static volatile TurtleCore instance;

    public static synchronized TurtleCore initialize(@NotNull Logger logger, @NotNull Environment environment, @NotNull File dataFolder, @NotNull File jarFile) {
        if (instance != null) return instance;
        instance = new TurtleCore(logger, environment, dataFolder, jarFile);
        return instance;
    }

    public static TurtleCore getCore() {
        return instance;
    }

    public static @NotNull File getJarFile() {
        try {
            return new File(Bootstrap.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Unexpected URISyntaxException", e);
        }
    }
}
