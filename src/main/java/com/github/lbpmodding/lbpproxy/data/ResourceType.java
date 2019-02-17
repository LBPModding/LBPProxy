package com.github.lbpmodding.lbpproxy.data;

import java.util.HashMap;
import java.util.Map;

public enum ResourceType {
    LEVEL("LVLb"),
    PLAN("PLNb"),
    TEX("TEX ");

    private static Map<String, ResourceType> headerToType = new HashMap<>();

    static {
        for (ResourceType current : ResourceType.values()) {
            headerToType.put(current.getMagic(), current);
        }
    }

    private final String magic;

    ResourceType(String magic) {
        this.magic = magic;
    }

    public String getMagic() {
        return magic;
    }

    public static ResourceType fromMagic(String header) {
        return headerToType.get(header);
    }
}
