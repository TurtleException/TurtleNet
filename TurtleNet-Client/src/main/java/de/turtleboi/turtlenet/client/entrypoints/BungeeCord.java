package de.turtleboi.turtlenet.client.entrypoints;

import de.turtleboi.turtlenet.api.environment.Environment;
import de.turtleboi.turtlenet.core.Bootstrap;
import de.turtleboi.turtlenet.core.TurtleCore;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * This is the entrypoint for a BungeeCord plugin. This class will be loaded by BungeeCord, as instructed per the
 * 'bungee.yml' resource, and if loading this application as a plugin succeeds, {@link BungeeCord#onEnable()} will be
 * called.
 */
public class BungeeCord extends Plugin {
    private TurtleCore core;

    @Override
    public void onEnable() {
        if (core != null)
            throw new IllegalStateException("Plugin enabled twice!");

        Logger logger = getLogger();
        Environment env = new de.turtleboi.turtlenet.api.environment.BungeeCord(
                getProxy().getVersion(),
                getProxy().getName(),
                getProxy().getConfig().isOnlineMode()
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
