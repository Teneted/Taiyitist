package com.taiyitistmc.injection.network.connection;

import java.net.SocketAddress;

public interface InjectionConnection {

    default String bridge$hostname() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setHostName(String hostName) {
        throw new IllegalStateException("Not implemented");
    }

    default SocketAddress getRawAddress() {
        throw new IllegalStateException("Not implemented");
    }
}
