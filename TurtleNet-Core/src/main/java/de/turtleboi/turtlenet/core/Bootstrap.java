package de.turtleboi.turtlenet.core;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URISyntaxException;

public final class Bootstrap {
    private static volatile TurtleCore instance;

    public static synchronized TurtleCore initialize() {
        if (instance != null) return instance;
        instance = null; // TODO: initialize
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
