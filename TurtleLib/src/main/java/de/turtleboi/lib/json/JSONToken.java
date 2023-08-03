package de.turtleboi.lib.json;

import org.jetbrains.annotations.NotNull;

public interface JSONToken {
    default boolean isValue() {
        return this instanceof JSONValue;
    }

    default boolean isObject() {
        return this instanceof JSONObject;
    }

    default boolean isArray() {
        return this instanceof JSONArray;
    }

    boolean isNumber();

    boolean isString();

    boolean isLiteral();

    @NotNull String toJson();

    @NotNull JSONToken deepCopy();
}
