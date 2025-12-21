package org.celestial_artistry.taiyitist.injection.network.connection;

import java.net.SocketAddress;

public interface InjectionConnection {

    default String bridge$hostname() {
        return null;
    }

    default void taiyitist$setHostName(String hostName) {

    }

    default SocketAddress getRawAddress() {
        return null;
    }
}
