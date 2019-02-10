package com.github.lbpmodding.lbpproxy.data;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum ResourceType {
    LEVEL("LVL"),
    PLAN("PLN"),
    TEX("TEX");

    private static Map<String, ResourceType> headerToType = new HashMap<>();

    static {
        for (ResourceType current : ResourceType.values()) {
            headerToType.put(current.getHeader(), current);
        }
    }

    @Getter
    private String header;

    ResourceType(String header) {
        this.header = header;
    }

    public static ResourceType fromHeader(String header) {
        return headerToType.get(header);
    }
}
