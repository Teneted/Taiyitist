package org.teneted.taiyitist.injection.server.network;

public interface InjectionServerConnectionListener {

    default void acceptConnections() {
        throw new IllegalStateException("Not implemented");
    }
}
