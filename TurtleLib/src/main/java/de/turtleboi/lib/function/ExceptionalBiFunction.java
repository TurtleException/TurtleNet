package de.turtleboi.lib.function;

import java.util.function.BiFunction;

/**
 * Represents a function that accepts two arguments and produces a result. This operation may throw checked exceptions,
 * which are relayed to the caller.
 * <br/> This is intended as a {@link BiFunction} that clearly communicates the necessity of exception handling. Furthermore,
 * it does not need to handle checked exceptions by wrapping them in {@link RuntimeException RuntimeExceptions}.
 * <p> This is a {@link java.util.function functional interface} whose functional method is {@link #apply(Object, Object)}.
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @see BiFunction
 * @see ExceptionalFunction
 */
@FunctionalInterface
public interface ExceptionalBiFunction<T, U, R> {
    /**
     * Applies this function to the given arguments.
     * @param t the first function argument.
     * @param u the second function argument.
     * @return the function result.
     */
    R apply(T t, U u) throws Exception;
}
