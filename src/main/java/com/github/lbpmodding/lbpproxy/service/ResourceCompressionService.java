package com.github.lbpmodding.lbpproxy.service;

import com.github.lbpmodding.lbpproxy.data.DecompressedResource;
import com.github.lbpmodding.lbpproxy.data.ResourceDescriptor;
import com.github.lbpmodding.lbpproxy.data.ResourceType;
import com.github.lbpmodding.lbpproxy.utility.HexUtilities;
import com.google.common.io.ByteStreams;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.InflaterInputStream;

public class ResourceCompressionService {

    public ResourceCompressionService() {
    }

    public ResourceType detect(ByteBuf source) {
        for (ResourceType currentType : ResourceType.values()) {
            byte[] currentHeader = currentType.getHeader();
            byte[] header = new byte[currentHeader.length];
            source.getBytes(0, header);
            if (Arrays.equals(currentHeader, header)) {
                return currentType;
            }
        }
        return null;
    }

    public DecompressedResource detectAndDecompress(String id, ByteBuf source) throws IOException {
        ResourceType type = detect(source);
        if (type == null) {
            return null; // Unknown
        }
        DecompressedResource resource;
        if (type.isCompressed()) {
            source.skipBytes(type.getHeader().length);
            if (type.hasDescriptor()) {
                resource = decompress(id, type, source);
            } else {
                resource = decompressSimple(id, type, source);
            }
        } else {
            resource = new DecompressedResource(id, null, ByteBufUtil.getBytes(source));
        }
        return resource;
    }

    // Decompress a resource without descriptor
    public DecompressedResource decompressSimple(String id, ResourceType type, ByteBuf source) throws IOException {
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
                outputBuffer.put(ByteStreams.toByteArray(inflaterInput));
            }
        }
        return new DecompressedResource(id, type, outputBuffer.array());
    }

    // Decompress a resource with descriptor
    public DecompressedResource decompress(String id, ResourceType type, ByteBuf source) throws IOException {
        long gameRevision = source.readUnsignedInt();

        // Ignored data
        source.skipBytes(4); // Offset to dependency list
        source.skipBytes(6); // Unknown flags, FIXME: size depends on game revision!

        // Decompress resource
        DecompressedResource resource = decompressSimple(id, type, source);

        // Parse dependencies
        long dependenciesCount = source.readUnsignedInt();
        Set<String> gameDependencies = new HashSet<>();
        Set<String> remoteDependencies = new HashSet<>();
        for (int i = 0; i < dependenciesCount; i++) {
            byte dependencyType = source.readByte();
            if (dependencyType == 0x01) {
                // SHA1
                byte[] sha1 = new byte[20];
                source.readBytes(sha1);
                remoteDependencies.add(HexUtilities.bytesToHexString(sha1));
            } else if (dependencyType == 0x02) {
                // GUID
                byte[] guid = new byte[4];
                source.readBytes(guid);
                gameDependencies.add(HexUtilities.bytesToHexString(guid));
            } else {
                throw new RuntimeException("Unknown dependency type " + HexUtilities.bytesToHexString(dependencyType));
            }
            source.skipBytes(4); // Unknown flags
        }

        ResourceDescriptor descriptor = new ResourceDescriptor(gameRevision, gameDependencies, remoteDependencies);
        resource.setDescriptor(descriptor);
        return resource;
    }
}
