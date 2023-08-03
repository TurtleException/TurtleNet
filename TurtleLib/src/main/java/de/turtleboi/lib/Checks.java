package de.turtleboi.lib;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Some basic check methods to minimize boilerplate code. */
public class Checks {
    /** Utility class */
    private Checks() { }

    /**
     * Checks if one or more objects are {@code null}. If either {@code obj} or any of {@code other} is {@code null}
     * this will return {@code false}, otherwise {@code true}.
     */
    public static boolean nonNulls(Object obj, Object... other) {
        if (obj == null) return false;
        if (other != null)
            for (Object o : other)
                if (o == null) return false;
        return true;
    }

    /**
     * Checks if an object is {@code null} and if so, throws a {@link NullPointerException} with the message
     * "{@code name} may not be null" with {@code name} being replaced by the first argument.
     * @param obj Object that should not, but could be {@code null}.
     * @param name A custom name that will be used in the exception message.
     * @throws NullPointerException if {@code obj} is {@code null}.
     */
    public static void nonNull(Object obj, @NotNull String name) throws NullPointerException {
        if (obj == null)
            throw new NullPointerException(name + " may not be null");
    }

    /** Returns {@code o1} if it is not {@code null}, otherwise returns {@code o2}. */
    public static <T> @NotNull T nullOr(@Nullable T o1, @NotNull T o2) {
        return o1 != null ? o1 : o2;
    }

    /** Only returns {@code true} if {@code obj} doesn't equal any object of {@code other}, otherwise {@code false}. */
    public static boolean equalsNeither(@NotNull Object obj, @NotNull Object other, @Nullable Object... others) {
        if (obj.equals(other)) return false;
        if (others != null)
            for (Object o : others)
                if (obj.equals(o)) return false;
        return true;
    }

    /** Only returns {@code true} if {@code obj} doesn't equal any object of {@code other}, otherwise {@code false}. */
    public static boolean equalsNeither(@NotNull Object obj, @NotNull Object[] other) {
        for (Object o : other)
            if (obj.equals(o)) return false;
        return true;
    }

    /** Returns {@code true} if {@code obj} equals at least one object of {@code other}, otherwise {@code false}. */
    public static boolean equalsAny(@NotNull Object obj, @NotNull Object other, @Nullable Object... others) {
        if (obj.equals(other)) return true;
        if (others != null)
            for (Object o : others)
                if (obj.equals(o)) return true;
        return false;
    }

    /** Returns {@code true} if {@code obj} equals at least one object of {@code other}, otherwise {@code false}. */
    public static boolean equalsAny(@NotNull Object obj, @NotNull Object[] other) {
        for (Object o : other)
            if (obj.equals(o)) return true;
        return false;
    }

    public static <T> boolean contains(T[] arr, Object obj) {
        for (T t : arr)
            if (t == obj)
                return true;
        return false;
    }

    public static <T> boolean containsAny(T[] arr, Object... obj) {
        for (T t : arr)
            for (Object o : obj)
                if (t == o)
                    return true;
        return false;
    }
}
