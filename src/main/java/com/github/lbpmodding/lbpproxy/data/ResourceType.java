package com.github.lbpmodding.lbpproxy.data;

import lombok.Getter;

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

    @Getter
    private String magic;

    ResourceType(String magic) {
        this.magic = magic;
    }

    public static ResourceType fromMagic(String header) {
        return headerToType.get(header);
    }
}
