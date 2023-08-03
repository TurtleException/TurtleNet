package de.turtleboi.lib.thread;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Queue;

/** A simple thread that executes {@link Runnable Runnables} that are provided by other Threads. */
public class QueueWorker extends Thread {
    private final Queue<Runnable> queue = new LinkedList<>();

    public QueueWorker() {
        this("QueueWorker-" + nextThreadNum());
    }

    public QueueWorker(@NotNull String name) {
        this(null, name);
    }

    public QueueWorker(@Nullable ThreadGroup group, @NotNull String name) {
        super(group, null, name);
        this.start();
    }

    public void queue(@NotNull Runnable task) {
        this.queue.add(task);
        this.notify();
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            // execute one task
            Runnable task = queue.poll();
            if (task != null)
                task.run();

            // sleep if there are no more tasks
            if (queue.isEmpty()) {
                try {
                    this.wait();
                } catch (InterruptedException ignored) { }
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        this.notify();
    }

    // Numbering threads without custom names
    private static int threadNum = 0;
    private static synchronized int nextThreadNum() {
        return threadNum++;
    }
}
