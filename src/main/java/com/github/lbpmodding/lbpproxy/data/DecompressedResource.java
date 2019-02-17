package com.github.lbpmodding.lbpproxy.data;

public class DecompressedResource {
    private ResourceDescriptor descriptor;
    private byte[] data;

    public DecompressedResource(ResourceDescriptor descriptor, byte[] data) {
        this.descriptor = descriptor;
        this.data = data;
    }

    public DecompressedResource(byte[] data) {
        this(null, data);
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
