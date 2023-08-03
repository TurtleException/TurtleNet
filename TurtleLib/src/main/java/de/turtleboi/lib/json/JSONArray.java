package de.turtleboi.lib.json;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

/**
 * Represents a JSON array as specified in <a href="https://www.ietf.org/rfc/rfc4627.txt">RFC4627</a> 2.3.
 * <br /> Elements are stored in order of insertion. Serializing this object by calling {@link JSONArray#toJson()}
 * will retain this order. Deserializing the produced String will create an array with the same order of elements.
 * <br /> This implementation is thread-safe.
 */
public class JSONArray implements JSONToken, List<JSONToken> {
    private final LinkedList<JSONToken> elements;
    private final Object lock = new Object();

    public JSONArray() {
        this.elements = new LinkedList<>();
    }

    public JSONArray(@NotNull JSONToken... elements) {
        this.elements = new LinkedList<>(Arrays.asList(elements));
    }

    public JSONArray(@NotNull Collection<JSONToken> elements) {
        this.elements = new LinkedList<>(elements);
    }

    /* - - - */

    @Override
    public @NotNull JSONToken deepCopy() {
        synchronized (lock) {
            return new JSONArray(elements.stream().map(JSONToken::deepCopy).toList());
        }
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

    @SuppressWarnings("unchecked")
    private <T> T[] asArray(@NotNull Class<T> type) {
        synchronized (lock) {
            T[] arr = (T[]) Array.newInstance(type, elements.size());
            for (int i = 0; i < arr.length; i++)
                arr[i] = (T) elements.get(i);
            return arr;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T[] asArray(@NotNull Class<T> type, @NotNull Function<JSONValue, T> mapper) {
        synchronized (lock) {
            T[] arr = (T[]) Array.newInstance(type, elements.size());
            for (int i = 0; i < arr.length; i++)
                arr[i] = mapper.apply((JSONValue) elements.get(i));
            return arr;
        }
    }

    public JSONToken[] asTokens() {
        return asArray(JSONToken.class);
    }

    public JSONValue[] asValues() {
        return asArray(JSONValue.class);
    }

    public JSONObject[] asObjects() {
        return asArray(JSONObject.class);
    }

    public JSONArray[] asArrays() {
        return asArray(JSONArray.class);
    }

    public String[] asStrings() {
        return asArray(String.class, JSONValue::getAsString);
    }

    public Number[] asNumbers() {
        return asArray(Number.class, JSONValue::getAsNumber);
    }

    public int[] asInts() {
        synchronized (lock) {
            int[] arr = new int[elements.size()];
            for (int i = 0; i < arr.length; i++)
                arr[i] = ((JSONValue) elements.get(i)).getAsInt();
            return arr;
        }
    }

    public long[] asLongs() {
        synchronized (lock) {
            long[] arr = new long[elements.size()];
            for (int i = 0; i < arr.length; i++)
                arr[i] = ((JSONValue) elements.get(i)).getAsLong();
            return arr;
        }
    }

    public float[] asFloats() {
        synchronized (lock) {
            float[] arr = new float[elements.size()];
            for (int i = 0; i < arr.length; i++)
                arr[i] = ((JSONValue) elements.get(i)).getAsFloat();
            return arr;
        }
    }

    public double[] asDoubles() {
        synchronized (lock) {
            double[] arr = new double[elements.size()];
            for (int i = 0; i < arr.length; i++)
                arr[i] = ((JSONValue) elements.get(i)).getAsDouble();
            return arr;
        }
    }

    public short[] asShots() {
        synchronized (lock) {
            short[] arr = new short[elements.size()];
            for (int i = 0; i < arr.length; i++)
                arr[i] = ((JSONValue) elements.get(i)).getAsShort();
            return arr;
        }
    }

    public byte[] asBytes() {
        synchronized (lock) {
            byte[] arr = new byte[elements.size()];
            for (int i = 0; i < arr.length; i++)
                arr[i] = ((JSONValue) elements.get(i)).getAsByte();
            return arr;
        }
    }

    public boolean[] asBooleans() {
        synchronized (lock) {
            boolean[] arr = new boolean[elements.size()];
            for (int i = 0; i < arr.length; i++)
                arr[i] = ((JSONValue) elements.get(i)).isBoolean();
            return arr;
        }
    }

    public @NotNull Integer[] asBoxedInts() {
        return asArray(Integer.class, JSONValue::getAsInt);
    }

    public @NotNull Long[] asBoxedLongs() {
        return asArray(Long.class, JSONValue::getAsLong);
    }

    public @NotNull Float[] asBoxedFloats() {
        return asArray(Float.class, JSONValue::getAsFloat);
    }

    public @NotNull Double[] asBoxedDoubles() {
        return asArray(Double.class, JSONValue::getAsDouble);
    }

    public @NotNull Short[] asBoxedShorts() {
        return asArray(Short.class, JSONValue::getAsShort);
    }

    public @NotNull Byte[] asBoxedBytes() {
        return asArray(Byte.class, JSONValue::getAsByte);
    }

    public @NotNull Boolean[] asBoxedBooleans() {
        return asArray(Boolean.class, JSONValue::getAsBoolean);
    }

    /* - - - */

    public void add(@NotNull String string) {
        this.add(new JSONValue(string));
    }

    public void add(int index, @NotNull String string) {
        this.add(index, new JSONValue(string));
    }

    public void add(@NotNull Number number) {
        this.add(new JSONValue(number));
    }

    public void add(int index, @NotNull Number number) {
        this.add(index, new JSONValue(number));
    }

    public void add(boolean b) {
        this.add(new JSONValue(b));
    }

    public void add(int index, boolean b) {
        this.add(index, new JSONValue(b));
    }

    public void addNull() {
        this.add(new JSONValue());
    }

    public void addNull(int index) {
        this.add(index, new JSONValue());
    }

    /* - - - */

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof JSONToken token))
            return false;
        synchronized (lock) {
            for (JSONToken element : elements)
                if (element.equals(token))
                    return true;
        }
        return false;
    }

    @Override
    public @NotNull Iterator<JSONToken> iterator() {
        LinkedList<JSONToken> copy = new LinkedList<>();
        synchronized (lock) {
            Collections.copy(copy, elements);
        }
        return copy.iterator();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @NotNull Object[] toArray() {
        synchronized (lock) {
             return elements.toArray();
        }
    }

    @SuppressWarnings({"NullableProblems", "SuspiciousToArrayCall"})
    @Override
    public <T> @NotNull T[] toArray(@NotNull T[] a) {
        synchronized (lock) {
            return elements.toArray(a);
        }
    }

    @Override
    public boolean add(JSONToken JSONToken) {
        synchronized (lock) {
            return elements.add(JSONToken);
        }
    }

    @Override
    public boolean remove(Object o) {
        if (!(o instanceof JSONToken))
            return false;
        synchronized (lock) {
            return elements.remove(o);
        }
    }

    public boolean remove(@NotNull String stringValue) {
        synchronized (lock) {
            for (int i = 0; i < elements.size(); i++) {
                JSONToken jsonToken = elements.get(i);

                if (!(jsonToken instanceof JSONValue value)) continue;
                if (!value.isString()) continue;
                if (!value.getAsString().equals(stringValue)) continue;

                elements.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean remove(@NotNull Number numberValue) {
        synchronized (lock) {
            for (int i = 0; i < elements.size(); i++) {
                JSONToken jsonToken = elements.get(i);

                if (!(jsonToken instanceof JSONValue value)) continue;
                if (!value.isNumber()) continue;
                if (!value.getAsNumber().equals(numberValue)) continue;

                elements.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean remove(boolean booleanValue) {
        synchronized (lock) {
            for (int i = 0; i < elements.size(); i++) {
                JSONToken jsonToken = elements.get(i);

                if (!(jsonToken instanceof JSONValue value)) continue;
                if (!value.isBoolean()) continue;
                if (value.getAsBoolean() != booleanValue) continue;

                elements.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean removeNull() {
        synchronized (lock) {
            for (int i = 0; i < elements.size(); i++) {
                JSONToken jsonToken = elements.get(i);

                if (!(jsonToken instanceof JSONValue value)) continue;
                if (!value.isNull()) continue;

                elements.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        synchronized (lock) {
            for (Object o : c)
                if (!this.contains(o))
                    return false;
            return true;
        }
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends JSONToken> c) {
        // add() handles synchronization. This operation can be performed concurrently
        for (JSONToken json : c)
            this.add(json);
        return true;
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends JSONToken> c) {
        synchronized (lock) {
            return elements.addAll(index, c);
        }
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        boolean updated = false;
        // remove() handles synchronization. This operation can be performed concurrently
        for (Object o : c)
            updated = updated || remove(o);
        return updated;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        synchronized (lock) {
            return elements.retainAll(c);
        }
    }

    @Override
    public void clear() {
        synchronized (lock) {
            elements.clear();
        }
    }

    @Override
    public @NotNull JSONToken get(int index) {
        synchronized (lock) {
            return elements.get(index);
        }
    }

    @Override
    public JSONToken set(int index, @Nullable JSONToken element) {
        if (element == null)
            element = new JSONValue();

        synchronized (lock) {
            return elements.set(index, element);
        }
    }

    @Override
    public void add(int index, @Nullable JSONToken element) {
        if (element == null)
            element = new JSONValue();

        synchronized (lock) {
            elements.add(index, element);
        }
    }

    @Override
    public JSONToken remove(int index) {
        synchronized (lock) {
            return elements.remove(index);
        }
    }

    public JSONToken tryRemove(int index) {
        synchronized (lock) {
            if (size() >= index) return null;
            return elements.remove(index);
        }
    }

    @Override
    public int indexOf(Object o) {
        if (!(o instanceof JSONToken))
            return -1;
        synchronized (lock) {
            return elements.indexOf(o);
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        if (!(o instanceof JSONToken))
            return -1;
        synchronized (lock) {
            return elements.lastIndexOf(o);
        }
    }

    @Override
    public @NotNull ListIterator<JSONToken> listIterator() {
        return this.listIterator(0);
    }

    @Override
    public @NotNull ListIterator<JSONToken> listIterator(int index) {
        LinkedList<JSONToken> copy = new LinkedList<>();
        synchronized (lock) {
            Collections.copy(copy, elements);
        }
        return copy.listIterator(index);
    }

    @Override
    public @NotNull List<JSONToken> subList(int fromIndex, int toIndex) {
        synchronized (lock) {
            return List.copyOf(elements.subList(fromIndex, toIndex));
        }
    }

    @Override
    public @NotNull String toJson() {
        return JsonParser.SC_BEGIN_ARRAY + String.join(String.valueOf(JsonParser.SC_VALUE_SEPARATOR), stream().map(JSONToken::toJson).toList()) + JsonParser.SC_END_ARRAY;
    }
}
