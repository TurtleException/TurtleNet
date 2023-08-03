package de.turtleboi.turtlenet.api.environment;

import org.jetbrains.annotations.NotNull;

/** An Environment provides information on the platform an instance of this application is running on. */
public interface Environment {
    @NotNull String getAsString();
}
