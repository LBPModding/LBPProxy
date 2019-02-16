package com.github.lbpmodding.lbpproxy.service;

import com.github.lbpmodding.lbpproxy.data.DecompressedData;
import com.github.lbpmodding.lbpproxy.data.Dependency;
import com.github.lbpmodding.lbpproxy.utility.HexUtilities;
import io.netty.buffer.ByteBuf;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.InflaterInputStream;

@NoArgsConstructor
@Slf4j
public class CustomCompressionService {

    public byte[] decompressMinimal(ByteBuf source) throws IOException {
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

    public DecompressedData decompress(ByteBuf source) throws IOException {
        long gameRevision = source.readUnsignedInt();

        // Ignored data
        source.skipBytes(4); // Offset to dependency list
        source.skipBytes(6); // Unknown flags, FIXME: size depends on game revision!

        // Parse data
        byte[] data = decompressMinimal(source);

        // Parse dependencies
        long dependenciesCount = source.readUnsignedInt();
        Set<Dependency> dependencies = new HashSet<>();
        for (int i = 0; i < dependenciesCount; i++) {
            byte dependencyType = source.readByte();
            if (dependencyType == 0x01) {
                // SHA1
                byte[] sha1 = new byte[20];
                source.readBytes(sha1);
                dependencies.add(new Dependency(HexUtilities.bytesToHexString(sha1)));
                log.info("SHA1 Dependency: " + HexUtilities.bytesToHexString(sha1));
            } else if (dependencyType == 0x02) {
                // GUID
                byte[] guid = new byte[4];
                source.readBytes(guid);
                dependencies.add(new Dependency(HexUtilities.bytesToHexString(guid)));
                log.info("GUID Dependency: " + HexUtilities.bytesToHexString(guid));
            } else {
                throw new RuntimeException("Unknown dependency type " + HexUtilities.bytesToHexString(dependencyType));
            }
            source.skipBytes(4); // Unknown flags
        }

        return new DecompressedData(gameRevision, data, dependencies);
    }
}
