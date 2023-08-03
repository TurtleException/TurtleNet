package de.turtleboi.lib.json;

import de.turtleboi.lib.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A lightweight parser for JSON data in compliance with <a href="https://www.ietf.org/rfc/rfc4627.txt">RFC4627</a>
 * @see JSONObject
 * @see JSONArray
 */
public class JsonParser {
    /* STRUCTURAL CHARACTERS */
    static final char SC_BEGIN_ARRAY     = '[';
    static final char SC_BEGIN_OBJECT    = '{';
    static final char SC_END_ARRAY       = ']';
    static final char SC_END_OBJECT      = '}';
    static final char SC_NAME_SEPARATOR  = ':';
    static final char SC_VALUE_SEPARATOR = ',';

    /* SPECIAL CHARACTERS */
    static final char ESCAPE = '\\';
    static final char QUOTATION_MARK = '\"';
    static final char[] WHITESPACES = {' ', '\t', '\n', '\r'};

    /* LITERAL VALUES */
    static final String LITERAL_FALSE = "false";
    static final String LITERAL_NULL  = "null";
    static final String LITERAL_TRUE  = "true";

    private JsonParser() { }

    public static JSONToken parse(String json) throws IllegalArgumentException {
        if (json == null)
            return new JSONValue();

        json = trim(json);

        if (json.startsWith(String.valueOf(QUOTATION_MARK)) && json.endsWith(String.valueOf(QUOTATION_MARK)) && !isEscaped(json, json.length() - 1))
            json = json.substring(1, json.length() - 1);

        if (json.startsWith(String.valueOf(SC_BEGIN_ARRAY)))
            return parseArray(json);
        else if (json.startsWith(String.valueOf(SC_BEGIN_OBJECT)))
            return parseObject(json);
        else
            return parseValue(json);
    }

    private static @NotNull JSONObject parseObject(@NotNull String str) throws IllegalArgumentException {
        if (!str.startsWith(String.valueOf(SC_BEGIN_OBJECT)))
            throw new IllegalArgumentException("Not a JSON object");
        if (!str.endsWith(String.valueOf(SC_END_OBJECT)))
            throw new IllegalArgumentException("Conflicting characters: JSON seems to start with object, but is not enclosed by it");

        // trim braces
        str = str.substring(1, str.length() - 1);

        Map<String, JSONToken> members = new LinkedHashMap<>();
        String nameBuffer = null;

        int  arrayLevel = 0;
        int objectLevel = 0;
        int start = 0; // start of current element
        boolean inValue = false; // currently in a 'name' (false) or 'value' (true)
        boolean inQuotationMarks = false;
        char[] chars = str.toCharArray();


        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            if (c == QUOTATION_MARK && !isEscaped(chars, i))
                inQuotationMarks = !inQuotationMarks;

            // skip any further character while between quotation marks
            if (inQuotationMarks) continue;

            if (c == SC_BEGIN_ARRAY && !isEscaped(chars, i))
                arrayLevel++;
            if (c == SC_END_ARRAY && !isEscaped(chars, i))
                arrayLevel--;
            if (c == SC_BEGIN_OBJECT && !isEscaped(chars, i))
                objectLevel++;
            if (c == SC_END_OBJECT && !isEscaped(chars, i))
                objectLevel--;

            // skip further checks if we're still in a nested value
            if (objectLevel > 1 || arrayLevel > 0) continue;

            if (inValue) {
                boolean endsWithMember = (c == SC_VALUE_SEPARATOR && !isEscaped(chars, i));
                boolean endsWithObject = (i + 1 < chars.length && chars[i + 1] == SC_END_OBJECT && isEscaped(chars, i + 1));

                // continue if end of value is not yet reached
                if (!endsWithMember && !endsWithObject) continue;

                inValue = false;

                String  stringValue = str.substring(start, i);
                JSONValue value = new JSONValue(stringValue);

                members.put(nameBuffer, value);
            } else {
                // skip if this is not an actual name separator
                if (c != SC_NAME_SEPARATOR || isEscaped(chars, i)) continue;

                nameBuffer = str.substring(start, i);

                if (!nameBuffer.startsWith(String.valueOf(QUOTATION_MARK)) || !nameBuffer.endsWith(String.valueOf(QUOTATION_MARK)) || /* end quotation may be escaped */ !isEscaped(chars, i - 1))
                    throw new IllegalArgumentException("Illegal object member name '" + nameBuffer + "'. Must be enclosed by quotation marks.");

                // trim quotation marks
                nameBuffer = nameBuffer.substring(1, nameBuffer.length() - 1);

                start = i + 1;
                inValue = true;
            }
        }

        return new JSONObject(members);
    }

    private static @NotNull JSONArray parseArray(@NotNull String str) throws IllegalArgumentException {
        if (!str.startsWith(String.valueOf(SC_BEGIN_ARRAY)))
            throw new IllegalArgumentException("Not a JSON array");
        if (!str.endsWith(String.valueOf(SC_END_ARRAY)))
            throw new IllegalArgumentException("Conflicting characters: JSON seems to start with array, but is not enclosed by it");

        // trim braces
        str = str.substring(1, str.length() - 1);

        List<String> elements = new ArrayList<>();

        int  arrayLevel = 0;
        int objectLevel = 0;
        int start = 0; // start of current element
        boolean inQuotationMarks = false;
        char[] chars = str.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            if (c == QUOTATION_MARK && !isEscaped(chars, i))
                inQuotationMarks = !inQuotationMarks;

            // skip any further character while between quotation marks
            if (inQuotationMarks) continue;

            if (c == SC_BEGIN_ARRAY && !isEscaped(chars, i))
                arrayLevel++;
            if (c == SC_END_ARRAY && !isEscaped(chars, i))
                arrayLevel--;
            if (c == SC_BEGIN_OBJECT && !isEscaped(chars, i))
                objectLevel++;
            if (c == SC_END_OBJECT && !isEscaped(chars, i))
                objectLevel--;

            // skip further checks if we're still in a nested value
            if (arrayLevel > 1 || objectLevel > 0) continue;

            // delimiter is either the separator ',' or end of the outermost array
            if (c == SC_VALUE_SEPARATOR || arrayLevel == 0) {
                // keep going if the value separator is escaped
                if (isEscaped(chars, i)) continue;

                String element = str.substring(start, i);
                elements.add(element);

                start = i + 1;
            }

            if (arrayLevel == 0) {
                // make sure there is no trailing data
                if (i < chars.length - 1)
                    throw new IllegalArgumentException("JSON may only be one object or array (except for nested values)");
            }
        }

        List<JSONToken> jsonElements = elements.stream().map(JsonParser::parse).toList();
        return new JSONArray(jsonElements);
    }

    private static @NotNull JSONValue parseValue(@NotNull String str) throws IllegalArgumentException {
        // check if the value is surrounded by quotation marks -> string
        if (str.startsWith(String.valueOf(JsonParser.QUOTATION_MARK)) && str.endsWith(String.valueOf(JsonParser.QUOTATION_MARK)) && !isEscaped(str.toCharArray(), str.length() - 1))
            return new JSONValue(str.substring(1, str.length() - 1));

        // try to parse literal
        switch (str) {
            case LITERAL_FALSE -> { return new JSONValue(false); }
            case LITERAL_NULL -> { return new JSONValue(); }
            case LITERAL_TRUE -> { return new JSONValue(true); }
            default -> { }
        }

        // try to parse a number
        if (str.contains(".")) {
            try {
                Double n = Double.parseDouble(str);

                // RFC 4627: "numeric values that cannot be represented as sequences of digits are not permitted"
                if (n.isInfinite() || n.isNaN())
                    n = null;

                if (n != null)
                    return new JSONValue(n);
            } catch (NumberFormatException ignored) { }
        } else {
            try {
                Long n = Long.parseLong(str);

                return new JSONValue(n);
            } catch (NumberFormatException ignored) { }
        }

        throw new IllegalArgumentException("Cannot parse value: " + str);
    }

    /** Removes any insignificant whitespaces from a JSON String. */
    private static @NotNull String trim(@NotNull String raw) {
        StringBuilder trimmedJsonBuilder = new StringBuilder();

        boolean inQuotation = false;
        char[] chars = raw.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            trimmedJsonBuilder.append(c);

            if (inQuotation) {
                if (c != QUOTATION_MARK) continue;

                // quotation may end here; keep going if the quotation marks are escaped
                if (isEscaped(chars, i)) continue;

                inQuotation = false;
                continue;
            }

            if (c == QUOTATION_MARK) {
                // ignore if the quotation marks completely surround the JSON
                if (i == 0 || i == chars.length - 1) continue;

                inQuotation = true;
                continue;
            }

            // delete added char if it is an insignificant whitespace
            if (Checks.equalsAny(c, WHITESPACES))
                trimmedJsonBuilder.deleteCharAt(trimmedJsonBuilder.length() - 1);
        }

        // delete surrounding quotation marks (only in pairs to not mess with validation)
        while (trimmedJsonBuilder.charAt(0) == QUOTATION_MARK && trimmedJsonBuilder.charAt(trimmedJsonBuilder.length() - 1) == QUOTATION_MARK) {
            trimmedJsonBuilder.deleteCharAt(0);
            trimmedJsonBuilder.deleteCharAt(trimmedJsonBuilder.length() - 1);
        }

        return trimmedJsonBuilder.toString();
    }

    static boolean isEscaped(char[] chars, int index) {
        int continuousEscapes = 0;
        for (int i = index - 1; i >= 0; i--) {
            if (chars[i] == ESCAPE)
                continuousEscapes++;
            else break;
        }

        return continuousEscapes % 2 == 1;
    }

    static boolean isEscaped(String str, int index) {
        int continuousEscapes = 0;
        for (int i = index - 1; i >= 0; i--) {
            if (str.charAt(i) == ESCAPE)
                continuousEscapes++;
            else break;
        }

        return continuousEscapes % 2 == 1;
    }
}
