package de.turtleboi.lib.json;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An immutable JSON value as specified in <a href="https://www.ietf.org/rfc/rfc4627.txt">RFC4627</a> 2.1. (More
 * precisely 2.4, 2.5 and the literal names mentioned in 2.1)
 * <br /> This implementation is thread-safe.
 * @see JSONObject
 * @see JSONArray
 */
public class JSONValue implements JSONToken {
    private static final int NOT_A_LITERAL = 0;
    private static final int LITERAL_FALSE = 1;
    private static final int LITERAL_NULL  = 2;
    private static final int LITERAL_TRUE  = 3;

    private final @NotNull String value;
    private final @Nullable Number number;
    private final int literal;

    public JSONValue() {
        this.value = JsonParser.LITERAL_NULL;
        this.literal = LITERAL_NULL;
        this.number = null;
    }

    public JSONValue(@NotNull String string) {
        this.value = string;
        this.literal = NOT_A_LITERAL;
        this.number = null;
    }

    public JSONValue(@NotNull Number number) {
        this.value = number.toString();
        this.literal = NOT_A_LITERAL;
        this.number = number;
    }

    public JSONValue(boolean b) {
        this.value = b ? JsonParser.LITERAL_TRUE : JsonParser.LITERAL_FALSE;
        this.literal = b ? LITERAL_TRUE : LITERAL_FALSE;
        this.number = null;
    }

    /* - - - */

    @Override
    public @NotNull JSONValue deepCopy() {
        // JSONValue is immutable
        return this;
    }

    /* - - - */

    @Override
    public boolean isNumber() {
        return number != null;
    }

    @Override
    public boolean isLiteral() {
        return literal != NOT_A_LITERAL;
    }

    public boolean isNull() {
        return literal == LITERAL_NULL;
    }

    public boolean isBoolean() {
        return literal == LITERAL_FALSE || literal == LITERAL_TRUE;
    }

    @Override
    public boolean isString() {
        return !isNull() && !isNumber() && !isLiteral();
    }

    public @NotNull String getAsString() {
        return value;
    }

    public @NotNull Number getAsNumber() throws IllegalStateException {
        if (number != null)
            return number;

        if (literal == LITERAL_FALSE)
            return 0;
        if (literal == LITERAL_TRUE)
            return 1;

        throw new IllegalStateException("Not a number");
    }

    public int getAsInt() throws IllegalStateException {
        return getAsNumber().intValue();
    }

    public long getAsLong() throws IllegalStateException {
        return getAsNumber().longValue();
    }

    public double getAsDouble() throws IllegalStateException {
        return getAsNumber().doubleValue();
    }

    public float getAsFloat() throws IllegalStateException {
        return getAsNumber().floatValue();
    }

    public short getAsShort() throws IllegalStateException {
        return getAsNumber().shortValue();
    }

    public byte getAsByte() throws IllegalStateException {
        return getAsNumber().byteValue();
    }

    public boolean getAsBoolean() throws IllegalStateException {
        if (literal == LITERAL_FALSE)
            return false;
        if (literal == LITERAL_TRUE)
            return true;
        throw new IllegalStateException("Not a boolean");
    }

    /* - - - */

    public @NotNull String toJson() {
        if (isLiteral())
            return value;
        if (isNumber())
            return String.valueOf(number);
        return JsonParser.QUOTATION_MARK + value + JsonParser.QUOTATION_MARK;
    }
}
