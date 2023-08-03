package de.turtleboi.lib;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Some methods to read, write and otherwise use varints.
 * <p> Varints are integers that can be represented with as few bytes as possible and therefore have a variable length.
 * This is done by cutting off leading zeros. Each byte stores 7 bits of an integer and a leading "continuation bit",
 * indicating whether the following byte is still part of the number. The 7 payload bits are then shifted into a result
 * integer to restore the value.
 * <br><br><b>An example</b>:
 * <pre> {@code
 * // a 32-bit integer (1,647,250):
 * 00000000 00011001 00100010 10010010
 *
 * // compact varint with the same value:
 * 10010010 11000101 01100100
 * ^        ^        ^ continuation bits
 * } </pre>
 * To restore an integer from these 2 bytes we just need to drop the continuation bits, reverse the endianness and
 * concatenate the remaining bits:
 * <pre> {@code
 * // drop continuation bits
 *  0010010  1000101  1100100
 * // reverse endianness
 *  1100100  1000101  0010010
 *
 * // concatenate & drop leading 0's
 * 0x110010010001010010010 == 1647250
 * } </pre>
 * <p> Using varints may be helpful for applications like networking, where some performance can be sacrificed to have
 * more compact data.
 * <p> Note that varints are not a data type and cannot be treated as such. They merely are a (possibly) more compact
 * way to store integers in byte arrays. Integers that make use of the most significant bits of their respective data
 * type will take up more space when represented as a varint, as the leading continuation-bits will take up 1/8 of each
 * byte. If such values are suspected then it is recommended not to use varints, if possible.
 */
public class VarInts {
    /** Utility class */
    private VarInts() { }

    /** Returns the necessary encoding size in bytes to represent a 32-bit varint. */
    public static int varIntSize(int i) {
        int result = 0;
        do {
            result++;
            // shift 7 bits to the right
            i >>>= 7;
            // check if there are still non-0 bits
        } while (i != 0);
        return result;
    }

    /**
     * Encodes an int into a 32-bit varint and writes it to a byte array at a given offset.
     * @param v varint value.
     * @param dst Destination array.
     * @param offset Current offset to write to.
     * @return Updated offset, after the varint has been written.
     */
    public static int putVarInt(int v, byte[] dst, int offset) {
        do {
            // encode next 7 bits + continuation bit
            int bits = v & 0x7F;

            // shift 7 bits to the right
            v >>>= 7;

            // create byte from bits and add continuation bit if there are still non-0 bits in v
            byte b = (byte) (bits + ((v != 0) ? 0x80 : 0));

            // write byte to destination and increment offset
            dst[offset++] = b;
        } while (v != 0);

        // return updated offset
        return offset;
    }

    /**
     * Reads a 32-bit varint from {@code src} at a provided offset and stores it in {@code val}. This will return the
     * new offset, after the int was read.
     * @param src Source array.
     * @param offset Current offset to start reading.
     * @param val The value of the varint will be written to the first index (0).
     * @return Updated offset, after the varint has been read.
     */
    public static int getVarInt(byte[] src, int offset, int[] val) {
        // result value, will be written 7 bits at a time
        int result = 0;
        // current shift for result, so new bits are properly read
        int shift = 0;
        // payload buffer for the 7 bits
        int b;

        do {
            if (shift >= 32)
                throw new IndexOutOfBoundsException("Varint-32 too long");

            // read 7 bits + continuation bit
            b = src[offset++];

            // write bits to result int
            result |= (b & 0x7F) << shift;
            // increment, so bits are shifted properly in the next iteration
            shift += 7;
        } while (/* check continuation bit */ (b & 0x80) != 0);

        // basically "return" int value
        val[0] = result;
        // actually return updated offset
        return offset;
    }

    public static byte[] intToBytes(int v) {
        byte[] bytes = new byte[Integer.BYTES];
        putVarInt(v, bytes, 0);
        return bytes;
    }

    public static int bytesToInt(byte[] bytes) {
        int[] val = new int[1];
        getVarInt(bytes, 0, val);
        return val[0];
    }

    public static byte[] intsToBytes(int[] v) {
        // maximum array size
        byte[] bytes = new byte[v.length * 5];
        int offset = 0;
        for (int i : v)
            offset = putVarInt(i, bytes, offset);
        return Arrays.copyOf(bytes, offset + 1);
    }

    public static byte[] intsToBytes(@NotNull Integer[] v) {
        // maximum array size
        byte[] bytes = new byte[v.length * 5];
        int offset = 0;
        for (int i : v)
            offset = putVarInt(i, bytes, offset);
        return Arrays.copyOf(bytes, offset + 1);
    }

    public static int[] bytesToInts(byte[] bytes) {
        int length = 0;
        for (byte b : bytes)
            if ((b & 0x80) == 0)
                length++;
        int[] data = new int[length];
        int offset = 0;
        int[] buffer = new int[1];
        for (int i = 0; i < data.length; i++) {
            offset = getVarInt(bytes, offset, buffer);
            data[i] = buffer[0];
        }
        return data;
    }

    public static Integer[] bytesToIntegers(byte[] bytes) {
        int length = 0;
        for (byte b : bytes)
            if ((b & 0x80) == 0)
                length++;
        Integer[] data = new Integer[length];
        int offset = 0;
        int[] buffer = new int[1];
        for (int i = 0; i < data.length; i++) {
            offset = getVarInt(bytes, offset, buffer);
            data[i] = buffer[0];
        }
        return data;
    }

    // The methods for var-longs work the same as the ones for var-ints. They are just more compact and not commented.

    /** Returns the necessary encoding size in bytes to represent a 64-bit varint. */
    public static int varLongSize(long l) {
        int result = 0;
        do {
            result++;
        } while ((l >>>= 7) != 0);
        return result;
    }

    /**
     * Encodes a long into a 64-bit varint and writes it to a byte array at a given offset.
     * @param v varint value.
     * @param dst Destination array.
     * @param offset Current offset to write to.
     * @return Updated offset, after the varint has been written.
     */
    public static int putVarLong(long v, byte[] dst, int offset) {
        do {
            dst[offset++] = (byte) ((v & 0x7F) + (((v >>>= 7) != 0) ? 0x80 : 0));
        } while (v != 0);
        return offset;
    }

    /**
     * Reads a 64-bit varint from {@code src} at a provided offset and stores it in {@code val}. This will return the
     * new offset, after the int was read.
     * @param src Source array.
     * @param offset Current offset to start reading.
     * @param val The value of the varint will be written to the first index (0).
     * @return Updated offset, after the varint has been read.
     */
    public static int getVarLong(byte[] src, int offset, long[] val) {
        long result = 0;
        int shift = 0, b;
        do {
            if (shift >= 64)
                throw new IndexOutOfBoundsException("Varint-64 too long");
            result |= (long) ((b = src[offset++]) & 0x7F) << shift;
            shift += 7;
        } while ((b & 0x80) != 0);
        val[0] = result;
        return offset;
    }

    public static byte[] longToBytes(long v) {
        byte[] bytes = new byte[Long.BYTES];
        putVarLong(v, bytes, 0);
        return bytes;
    }

    public static long bytesToLong(byte[] bytes) {
        long[] val = new long[1];
        getVarLong(bytes, 0, val);
        return val[0];
    }

    public static byte[] longsToBytes(long[] v) {
        // maximum array size
        byte[] bytes = new byte[v.length * 10];
        int offset = 0;
        for (long i : v)
            offset = putVarLong(i, bytes, offset);
        return Arrays.copyOf(bytes, offset + 1);
    }

    public static byte[] longsToBytes(@NotNull Long[] v) {
        // maximum array size
        byte[] bytes = new byte[v.length * 10];
        int offset = 0;
        for (long i : v)
            offset = putVarLong(i, bytes, offset);
        return Arrays.copyOf(bytes, offset + 1);
    }

    public static long[] bytesToLongs(byte[] bytes) {
        int length = 0;
        for (byte b : bytes)
            if ((b & 0x80) == 0)
                length++;
        long[] data = new long[length];
        int offset = 0;
        long[] buffer = new long[1];
        for (int i = 0; i < data.length; i++) {
            offset = getVarLong(bytes, offset, buffer);
            data[i] = buffer[0];
        }
        return data;
    }

    public static Long[] bytesToBoxedLongs(byte[] bytes) {
        int length = 0;
        for (byte b : bytes)
            if ((b & 0x80) == 0)
                length++;
        Long[] data = new Long[length];
        int offset = 0;
        long[] buffer = new long[1];
        for (int i = 0; i < data.length; i++) {
            offset = getVarLong(bytes, offset, buffer);
            data[i] = buffer[0];
        }
        return data;
    }
}