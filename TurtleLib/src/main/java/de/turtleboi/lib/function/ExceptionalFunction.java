package de.turtleboi.lib.function;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Represents a function that accepts one argument and produces a result. This operation may throw checked exceptions,
 * which are relayed to the caller.
 * <br/> This is intended as a {@link Function} that clearly communicates the necessity of exception handling. Furthermore,
 * it does not need to handle checked exceptions by wrapping them in {@link RuntimeException RuntimeExceptions}.
 * <p> This is a {@link java.util.function functional interface} whose functional method is {@link #apply(Object)}.
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @see Function
 */
@FunctionalInterface
public interface ExceptionalFunction<T, R> {
    /**
     * Applies this function to the given argument.
     * @param t the function argument
     * @return the function result
     * @see Function#apply(Object)
     */
    R apply(T t) throws Exception;

    /**
     * Returns a composed function that first applies the {@code before} function to its input, and then applies this
     * function to the result. If evaluation of either function throws an exception, it is relayed to the caller of the
     * composed function.
     * @param <V> the type of input to the {@code before} function, and to the composed function
     * @param before the function to apply before this function is applied
     * @return a composed function that first applies the {@code before} function and then applies this function
     * @throws NullPointerException if before is null
     *
     * @see #andThen(ExceptionalFunction)
     * @see Function#compose(Function)
     */
    default <V> @NotNull ExceptionalFunction<V, R> compose(@NotNull(exception = NullPointerException.class) ExceptionalFunction<? super V, ? extends T> before) {
        return (V v) -> apply(before.apply(v));
    }

    /**
     * Returns a composed function that first applies this function to its input, and then applies the {@code after}
     * function to the result. If evaluation of either function throws an exception, it is relayed to the caller of the
     * composed function.
     * @param <V> the type of output of the {@code after} function, and of the composed function
     * @param after the function to apply after this function is applied
     * @return a composed function that first applies this function and then applies the {@code after} function
     * @throws NullPointerException if after is null
     * @see #compose(ExceptionalFunction)
     */
    default <V> @NotNull ExceptionalFunction<T, V> andThen(@NotNull(exception = NullPointerException.class) ExceptionalFunction<? super R, ? extends V> after) {
        return (T t) -> after.apply(apply(t));
    }

    /**
     * Returns a function that always returns its input argument.
     * @param <T> the type of the input and output objects to the function
     * @return a function that always returns its input argument
     */
    static <T> ExceptionalFunction<T, T> identity() {
        return t -> t;
    }
}