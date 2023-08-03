package de.turtleboi.turtlenet.server;

import de.turtleboi.turtlenet.api.environment.Environment;
import de.turtleboi.turtlenet.core.Bootstrap;
import de.turtleboi.turtlenet.core.TurtleCore;

import java.io.File;
import java.util.logging.Logger;

/** Server main class. */
public final class Main {
    static {
        System.out.print("Starting TurtleServer");
        System.out.printf(" (core v%s)...%n", TurtleCore.VERSION);
    }

    public static TurtleServer singleton;

    /** Called by the JVM when starting this program. */
    public static void main(String[] args) throws Exception {
        if (singleton != null)
            throw new IllegalStateException("Cannot initialize multiple times.");

        final Logger   logger = Logger.getLogger("SERVER");
        final Environment env = () -> "server";
        final File    jarFile = Bootstrap.getJarFile();
        final File dataFolder = jarFile.getParentFile();

        final TurtleCore core = Bootstrap.initialize(logger, env, jarFile, dataFolder);

        singleton = new TurtleServer(core);
        singleton.run();
    }
}
