package de.turtleboi.lib.tuple;

import java.util.Map;

public record Pair<A, B>(
        A first,
        B second
) implements Map.Entry<A, B> {
        @Override
        public A getKey() {
                return first();
        }

        @Override
        public B getValue() {
                return second();
        }

        @Override
        public B setValue(Object value) {
                throw new UnsupportedOperationException("Immutable");
        }
}
