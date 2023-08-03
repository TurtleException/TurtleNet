package de.turtleboi.lib;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/** Some utilities for bitwise operations, mostly focused on compression. */
public class Bits {
    private Bits() { }

    public static byte[] toBytes(boolean[] b) {
        byte[] bytes = new byte[(b.length + 8 - 1) / 8];
        int shift = 7;
        int index = 0;
        for (boolean val : b) {
            bytes[index] |= (val ? 1 : 0) << shift--;
            if (shift < 0) {
                shift = 7;
                index++;
            }
        }
        return bytes;
    }

    public static byte[] toBytes(@NotNull Boolean[] b) {
        byte[] bytes = new byte[(b.length + 8 - 1) / 8];
        int shift = 7;
        int index = 0;
        for (boolean val : b) {
            bytes[index] |= (val ? 1 : 0) << shift--;
            if (shift < 0) {
                shift = 7;
                index++;
            }
        }
        return bytes;
    }

    public static boolean[] ofBytes(byte[] bytes) {
        return ofBytesOffset(bytes, 0);
    }

    public static boolean[] ofBytesLimited(byte[] bytes, int l) {
        return ofBytes(bytes, l, 0);
    }

    public static boolean[] ofBytesOffset(byte[] bytes, int offset) {
        return ofBytes(bytes, (bytes.length - offset) * 8, offset);
    }

    public static boolean[] ofBytes(byte[] bytes, int l, int offset) {
        boolean[] val = new boolean[l];
        int shift = 7;
        int index = 0;
        for (int i = offset; i < bytes.length; i++) {
            val[index++] = (bytes[i] >> shift--) != 0;
            if (shift < 0)
                shift = 7;
        }
        return val;
    }

    public static Boolean[] ofBytesBoxed(byte[] bytes) {
        return ofBytesBoxed(bytes, bytes.length * 8);
    }

    public static Boolean[] ofBytesBoxed(byte[] bytes, int l) {
        Boolean[] val = new Boolean[l];
        int shift = 7;
        int index = 0;
        for (byte b : bytes) {
            val[index++] = (b >> shift--) != 0;
            if (shift < 0)
                shift = 7;
        }
        return val;
    }

    public static long setBit(final long n, @Range(from = 0, to = Long.SIZE - 1) final int bit, final boolean value) {
        if (value)
            return n | (1L << bit);
        else
            return n & ~(1L << bit);
    }
}
