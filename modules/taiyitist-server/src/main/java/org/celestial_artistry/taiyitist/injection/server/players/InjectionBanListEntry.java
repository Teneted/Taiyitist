package org.celestial_artistry.taiyitist.injection.server.players;

import java.util.Date;

public interface InjectionBanListEntry {

    default Date getCreated() {
        return null;
    }
}
