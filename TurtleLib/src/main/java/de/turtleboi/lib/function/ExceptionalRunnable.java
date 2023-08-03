package de.turtleboi.lib.function;

/** A {@link Runnable} that may throw checked exceptions. */
@FunctionalInterface
public interface ExceptionalRunnable {
    /** @see Runnable#run() */
    void run() throws Exception;
}
