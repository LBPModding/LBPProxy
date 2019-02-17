package com.github.lbpmodding.lbpproxy.data;

import java.util.Set;

public class ResourceDescriptor {
    private long gameRevision;
    private Set<String> gameDependencies;
    private Set<String> remoteDependencies;

    public ResourceDescriptor(long gameRevision, Set<String> gameDependencies, Set<String> remoteDependencies) {
        this.gameRevision = gameRevision;
        this.gameDependencies = gameDependencies;
        this.remoteDependencies = remoteDependencies;
    }

    public long getGameRevision() {
        return gameRevision;
    }

    public void setGameRevision(long gameRevision) {
        this.gameRevision = gameRevision;
    }

    public Set<String> getGameDependencies() {
        return gameDependencies;
    }

    public void setGameDependencies(Set<String> gameDependencies) {
        this.gameDependencies = gameDependencies;
    }

    public Set<String> getRemoteDependencies() {
        return remoteDependencies;
    }

    public void setRemoteDependencies(Set<String> remoteDependencies) {
        this.remoteDependencies = remoteDependencies;
    }
}
