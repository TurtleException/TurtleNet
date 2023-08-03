package de.turtleboi.turtlenet.api.environment;

import org.jetbrains.annotations.NotNull;

public record Spigot(
        @NotNull String version,
        @NotNull String bukkitVersion,
        @NotNull String name,
        @NotNull String ip,
        int port,
        @NotNull String motd
) implements Environment {
    @Override
    public @NotNull String getAsString() {
        return "spigot-" + version();
    }
}
