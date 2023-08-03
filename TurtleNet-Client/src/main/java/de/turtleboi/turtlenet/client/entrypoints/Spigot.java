package de.turtleboi.turtlenet.client.entrypoints;

import de.turtleboi.turtlenet.api.environment.Environment;
import de.turtleboi.turtlenet.core.Bootstrap;
import de.turtleboi.turtlenet.core.TurtleCore;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * This is the entrypoint for a Spigot plugin. This class will be loaded by Spigot, as instructed per the 'plugin.yml'
 * resource, and if loading this application as a plugin succeeds, {@link Spigot#onEnable()} will be called.
 */
@SuppressWarnings("unused")
public class Spigot extends JavaPlugin {
    private TurtleCore core;

    @Override
    public void onEnable() {
        if (core != null)
            throw new IllegalStateException("Plugin enabled twice!");

        Logger logger = getLogger();
        Environment env = new de.turtleboi.turtlenet.api.environment.Spigot(
                getServer().getVersion(),
                getServer().getBukkitVersion(),
                getServer().getName(),
                getServer().getIp(),
                getServer().getPort(),
                getServer().getMotd()
        );
        File dataFolder = getDataFolder();
        File jarFile = Bootstrap.getJarFile();

        try {
            core = Bootstrap.initialize(logger, env, dataFolder, jarFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        if (core == null) return;

        try {
            core.shutdown(4, TimeUnit.SECONDS);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
