package de.turtleboi.turtlenet.api.environment;

import org.jetbrains.annotations.NotNull;

public record BungeeCord(
        @NotNull String version,
        @NotNull String name,
        boolean onlineMode
) implements Environment {
    @Override
    public @NotNull String getAsString() {
        return "bungeecord-" + version();
    }
}
