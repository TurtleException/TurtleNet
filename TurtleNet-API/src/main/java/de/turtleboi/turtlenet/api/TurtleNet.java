package de.turtleboi.turtlenet.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/** API interface to the application core. */
public interface TurtleNet {
    /**
     * This instance may not be re-used after shutting down. Doing so would result in unpredictable behaviour.
     * Resource caches are not emptied on shutdown, and while this is not recommended, they can technically still be
     * accessed afterwards.
     * <p> This method will block the calling thread until the application is shut down or the timeout is exceeded.
     * @throws IOException if such an exception is thrown while shutting down an underlying service.
     * @see #shutdownNow()
     */
    void shutdown(long timeout, @NotNull TimeUnit unit) throws IOException;

    /**
     * Forces this instance to shut down. A safe disconnect cannot be guaranteed.
     * @throws IOException if such an exception is thrown while shutting down an underlying service.
     * @see #shutdown(long, TimeUnit)
     */
    void shutdownNow() throws IOException;
}
