package de.turtleboi.lib.thread;

import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

/**
 * A simple Thread that continuously repeats executing a {@link Runnable} until it is interrupted by a provided
 * {@link BooleanSupplier} or via {@link Thread#interrupt()}.
 */
public class Worker extends Thread {
    private final BooleanSupplier condition;
    private final Runnable task;

    public Worker(@NotNull BooleanSupplier condition, @NotNull Runnable task) {
        this.condition = condition;
        this.task = task;

        this.setDaemon(true);
        this.start();
    }

    @Override
    public void run() {
        while (this.condition.getAsBoolean() && !this.isInterrupted())
            this.task.run();
    }
}
