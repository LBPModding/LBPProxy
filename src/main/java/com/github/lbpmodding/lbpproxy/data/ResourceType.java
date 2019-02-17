package com.github.lbpmodding.lbpproxy.data;

import java.nio.charset.StandardCharsets;

public enum ResourceType {
    LEVEL("LVLb", true, true),
    PLAN("PLNb", true, true),
    TEXTURE("TEX ", true, false),
    PHOTO(new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}, false, false); // JPEG Header

    private final byte[] header;
    private final boolean compressed;
    private final boolean descriptor;

    ResourceType(byte[] header, boolean compressed, boolean descriptor) {
        this.header = header;
        this.compressed = compressed;
        this.descriptor = descriptor;
    }

    ResourceType(String header, boolean compressed, boolean descriptor) {
        this(header.getBytes(StandardCharsets.UTF_8), compressed, descriptor);
    }

    public byte[] getHeader() {
        return header;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public boolean hasDescriptor() {
        return descriptor;
    }
}
