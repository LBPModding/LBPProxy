package com.github.lbpmodding.lbpproxy.data;

public class DecompressedResource {
    private final String id;
    private final ResourceType type;
    private ResourceDescriptor descriptor;
    private byte[] data;

    public DecompressedResource(String id, ResourceType type, ResourceDescriptor descriptor, byte[] data) {
        this.id = id;
        this.type = type;
        this.descriptor = descriptor;
        this.data = data;
    }

    public DecompressedResource(String id, ResourceType type, byte[] data) {
        this(id, type, null, data);
    }

    public String getId() {
        return id;
    }

    public ResourceType getType() {
        return type;
    }

    public boolean hasDescriptor() {
        return descriptor != null;
    }

    public ResourceDescriptor getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(ResourceDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
