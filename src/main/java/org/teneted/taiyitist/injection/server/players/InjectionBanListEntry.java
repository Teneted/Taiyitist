package org.teneted.taiyitist.injection.server.players;

import java.util.Date;

public interface InjectionBanListEntry {

    default Date getCreated() {
        throw new IllegalStateException("Not implemented");
    }
}
