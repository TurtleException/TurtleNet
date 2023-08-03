package de.turtleboi.turtlenet.core.util;

import de.turtleboi.turtlenet.core.Bootstrap;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

public class ResourceUtil {
    /** Utility class */
    private ResourceUtil() { }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void saveDefault(@NotNull File file, @NotNull String name) throws IOException {
        if (file.exists()) return;
        file.getParentFile().mkdirs();
        Files.copy(getResource(name), file.toPath());
    }

    public static @NotNull String getProperty(@NotNull String filename, @NotNull String property) {
        try {
            Properties properties = new Properties();
            properties.load(getResource(filename));
            return properties.getProperty(property);
        } catch (IOException e) {
            System.out.println("Unable to obtain property '" + property + "' from resource '" + filename + ".");
            throw new RuntimeException(e);
        }
    }

    public static InputStream getResource(String name) {
        return Bootstrap.class.getClassLoader().getResourceAsStream(name);
    }
}
