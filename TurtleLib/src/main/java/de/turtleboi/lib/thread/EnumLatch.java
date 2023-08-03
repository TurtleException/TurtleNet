package de.turtleboi.lib.thread;

import de.turtleboi.lib.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A synchronization aid that allows one or more threads to wait until an enum field has been updated to a certain value
 * via the {@link #set(Enum)} method.
 * @param <E> Enum type.
 */
public final class EnumLatch<E extends Enum<E>> {
    private final Lock lock;
    private final Map<Thread, Pair<E, Condition>> conditions;
    private volatile E value;

    public EnumLatch(@NotNull E initialValue) {
        this.lock       = new ReentrantLock();
        this.conditions = new HashMap<>();
        this.value      = initialValue;
    }

    public void await(@NotNull E value) throws InterruptedException {
        lock.lock();

        try {
            // immediately return if the condition is already met
            if (this.value.equals(value))
                return;

            // block thread
            registerCondition(value).await();
        } finally {
            conditions.remove(Thread.currentThread());
            lock.unlock();
        }
    }

    public boolean await(@NotNull E value, long time, @NotNull TimeUnit unit) throws InterruptedException {
        lock.lock();


        try {
            // immediately return if the value is already met
            if (this.value.equals(value))
                return false;

            // block thread
            return registerCondition(value).await(time, unit);
        } finally {
            conditions.remove(Thread.currentThread());
            lock.unlock();
        }
    }

    private Condition registerCondition(@NotNull E value) {
        final Thread currentThread = Thread.currentThread();
        final Condition  condition = lock.newCondition();

        // register condition
        conditions.put(currentThread, new Pair<>(value, condition));

        return condition;
    }

    public void set(@NotNull E value) {
        lock.lock();

        try {
            this.updateConditions(value);
        } finally {
            lock.unlock();
        }
    }

    public boolean update(@NotNull E from, @NotNull E to) {
        lock.lock();

        try {
            if (!this.value.equals(from))
                return false;

            this.updateConditions(to);
        } finally {
            lock.unlock();
        }
        return true;
    }

    private void updateConditions(@NotNull E value) {
        // must be locked during invocation

        this.value = value;

        for (Pair<E, Condition> condition : conditions.values()) {
            // check if the condition awaits the new value
            if (!condition.first().equals(value)) continue;

            condition.second().signalAll();
        }
    }

    public @NotNull E get() {
        return this.value;
    }
}
