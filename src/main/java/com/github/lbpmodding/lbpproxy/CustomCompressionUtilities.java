package com.github.lbpmodding.lbpproxy;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.InflaterInputStream;

public final class CustomCompressionUtilities {

    private CustomCompressionUtilities() {
    }

    public static byte[] decompressMinimal(ByteBuf source) throws IOException {
        source.skipBytes(2); // Skip unknown: 0x00 0x01
        int entryCount = source.readUnsignedShort();
        int[] compressedSizes = new int[entryCount];
        int totalUncompressedSize = 0;
        for (int i = 0; i < entryCount; i++) {
            compressedSizes[i] = source.readUnsignedShort(); // Compressed size
            totalUncompressedSize += source.readUnsignedShort(); // Uncompressed size
        }
        ByteBuffer outputBuffer = ByteBuffer.allocate(totalUncompressedSize);
        for (int i = 0; i < entryCount; i++) {
            byte[] compressedStream = new byte[compressedSizes[i]];
            source.readBytes(compressedStream); // Compressed stream
            try (ByteArrayInputStream byteInput = new ByteArrayInputStream(compressedStream);
                 InflaterInputStream inflaterInput = new InflaterInputStream(byteInput)) {
                outputBuffer.put(inflaterInput.readAllBytes());
            }
        }
        return outputBuffer.array();
    }
}
