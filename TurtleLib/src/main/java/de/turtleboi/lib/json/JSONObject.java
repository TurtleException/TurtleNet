package de.turtleboi.lib.json;

import de.turtleboi.lib.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.*;

/**
 * Represents a JSON object as specified in <a href="https://www.ietf.org/rfc/rfc4627.txt">RFC4627</a> 2.2.
 * <br /> Members are stored in order of insertion. Serializing this object by calling {@link JSONObject#toJson()}
 * will retain this order. Deserializing the produced String will create an object with the same order of members.
 * <br /> This implementation is thread-safe.
 */
public class JSONObject implements JSONToken, Map<String, JSONToken> {
    private final ArrayList<Pair<String, JSONToken>> members;
    private final Object lock = new Object();

    public JSONObject() {
        this.members = new ArrayList<>();
    }

    public JSONObject(@NotNull Map<String, JSONToken> members) {
        this.members = new ArrayList<>(members.size());
        this.putAll(members);
    }

    /* - - - */

    @Override
    public @NotNull JSONObject deepCopy() {
        JSONObject object = new JSONObject();
        synchronized (lock) {
            for (Pair<String, JSONToken> member : members)
                object.set(member.first(), member.second().deepCopy());
        }
        return object;
    }

    /* - - - */

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override
    public boolean isString() {
        return false;
    }

    @Override
    public boolean isLiteral() {
        return false;
    }

    /* - - - */

    public void set(@NotNull String name, @NotNull JSONToken value) {
        this.put(name, value);
    }

    public void set(@NotNull String name, @NotNull String stringValue) {
        this.set(name, new JSONValue(stringValue));
    }

    public void set(@NotNull String name, @NotNull Number numberValue) {
        this.set(name, new JSONValue(numberValue));
    }

    public void set(@NotNull String name, boolean booleanValue) {
        this.set(name, new JSONValue(booleanValue));
    }

    public void setNull(@NotNull String name) {
        this.set(name, new JSONValue());
    }

    /* - - - */

    public @NotNull JSONArray makeArray(@NotNull String name) {
        synchronized (lock) {
            JSONToken current = this.get(name);
            if (current instanceof JSONArray arr)
                return arr;

            JSONArray newArray = new JSONArray();
            this.set(name, newArray);
            return newArray;
        }
    }

    /* - - - */

    @Override
    public int size() {
        return members.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof String name))
            return false;
        synchronized (lock) {
            for (Pair<String, JSONToken> member : members)
                if (member.first().equals(name))
                    return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        if (!(value instanceof JSONToken val))
            return false;
        synchronized (lock) {
            for (Pair<String, JSONToken> member : members)
                if (member.second().equals(val))
                    return true;
        }
        return false;
    }

    /* - - - */

    @Override
    public JSONToken get(Object key) {
        if (!(key instanceof String name))
            return null;
        synchronized (lock) {
            for (Pair<String, JSONToken> member : members)
                if (member.first().equals(name))
                    return member.second();
        }
        return null;
    }

    public JSONObject getObject(@NotNull String name) {
        return ((JSONObject) this.get(name));
    }

    public JSONArray getArray(@NotNull String name) {
        return ((JSONArray) this.get(name));
    }

    public JSONValue getValue(@NotNull String name) {
        return ((JSONValue) this.get(name));
    }

    public String getString(@NotNull String name) {
        return this.getValue(name).getAsString();
    }

    public Number getNumber(@NotNull String name) {
        return this.getValue(name).getAsNumber();
    }

    public int getInt(@NotNull String name) {
        return this.getNumber(name).intValue();
    }

    public long getLong(@NotNull String name) {
        return this.getNumber(name).longValue();
    }

    public float getFloat(@NotNull String name) {
        return this.getNumber(name).floatValue();
    }

    public double getDouble(@NotNull String name) {
        return this.getNumber(name).doubleValue();
    }

    public short getShort(@NotNull String name) {
        return this.getNumber(name).shortValue();
    }

    public byte getByte(@NotNull String name) {
        return this.getNumber(name).byteValue();
    }

    public boolean getBoolean(@NotNull String name) {
        return ((JSONValue) this.get(name)).isBoolean();
    }

    /* - - - */

    @Override
    public @Nullable JSONToken put(String key, JSONToken value) {
        Pair<String, JSONToken> member = new Pair<>(key, value);
        synchronized (lock) {
            for (int i = 0; i < members.size(); i++) {
                if (!members.get(i).first().equals(key)) continue;
                return Optional.ofNullable(members.set(i, member))
                        .map(Pair::second)
                        .orElse(null);
            }
            members.add(member);
        }
        return null;
    }

    public @Nullable JSONToken put(String key, JSONToken value, @Range(from = 0, to = Integer.MAX_VALUE) int index) {
        Pair<String, JSONToken> member = new Pair<>(key, value);

        synchronized (lock) {
            JSONToken removed = this.remove(key);

            if (index <= members.size())
                members.add(index, member);
            else
                members.add(member);
            return removed;
        }
    }

    @Override
    public JSONToken remove(Object key) {
        if (!(key instanceof String name))
            return null;
        synchronized (lock) {
            for (int i = 0, membersSize = members.size(); i < membersSize; i++) {
                if (members.get(i).first().equals(name)) {
                    members.remove(i);
                    return members.get(i).second();
                }
            }
        }
        return null;
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends JSONToken> m) {
        // put() handles synchronization. This operation can be performed concurrently
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        synchronized (lock) {
            members.clear();
        }
    }

    // TODO: this should return a set VIEW
    @Override
    public @NotNull Set<String> keySet() {
        Set<String> names = new LinkedHashSet<>();
        synchronized (lock) {
            for (Pair<String, JSONToken> member : members)
                names.add(member.first());
        }
        return names;
    }

    // TODO: this should return a collection VIEW
    @Override
    public @NotNull Collection<JSONToken> values() {
        List<JSONToken> values = new ArrayList<>();
        synchronized (lock) {
            for (Pair<String, JSONToken> member : members)
                values.add(member.second());
        }
        return values;
    }

    @NotNull
    @Override
    public Set<Entry<String, JSONToken>> entrySet() {
        synchronized (lock) {
            return new LinkedHashSet<>(members);
        }
    }

    /* - - - */

    @Override
    public @NotNull String toJson() {
        return JsonParser.SC_BEGIN_OBJECT
               + String.join(
                        String.valueOf(JsonParser.SC_VALUE_SEPARATOR),
                        members.stream().map(member ->
                                JsonParser.QUOTATION_MARK
                                + member.first()
                                + JsonParser.QUOTATION_MARK
                                + JsonParser.SC_NAME_SEPARATOR
                                + member.second().toJson()
                        ).toList())
               + JsonParser.SC_END_OBJECT;
    }
}
