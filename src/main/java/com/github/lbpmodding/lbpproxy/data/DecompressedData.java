package com.github.lbpmodding.lbpproxy.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
public class DecompressedData {
    private long gameRevision;
    private byte[] data;
    private Set<Dependency> dependencies;
}
