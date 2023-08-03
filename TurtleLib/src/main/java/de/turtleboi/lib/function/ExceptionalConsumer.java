package de.turtleboi.lib.function;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A {@link Consumer} that may throw checked Exceptions.
 * @param <T> the type of the input to the operation
 */
@FunctionalInterface
public interface ExceptionalConsumer<T> {
    /** @see Consumer#accept(Object) */
    void accept(T t) throws Exception;

    /** @see Consumer#andThen(Consumer) */
    default ExceptionalConsumer<T> andThen(ExceptionalConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> { accept(t); after.accept(t); };
    }
}