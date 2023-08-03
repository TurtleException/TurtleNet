package de.turtleboi.lib;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.UUID;

/** Some basic utilities for {@link UUID UUIDs}. */
public class UUIDUtils {
    /**
     * Parses a UUID from an array of bytes.
     * @param bytes The UUID bytes in little endian order.
     * @return Parsed UUID
     * @see #toBytes(UUID)
     */
    public static @NotNull UUID fromBytes(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long msb = bb.getLong();
        long lsb = bb.getLong();
        return new UUID(msb, lsb);
    }

    /**
     * Creates a new byte array that contains the UUID's bytes in little endian order.
     * @param uuid The UUID.
     * @return New byte array
     * @see #fromBytes(byte[])
     */
    public static byte[] toBytes(@NotNull UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}
