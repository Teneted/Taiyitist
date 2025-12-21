package org.celestial_artistry.taiyitist.injection.server.level;

import org.celestial_artistry.taiyitist.bukkit.BukkitCallbackExecutor;

public interface InjectionChunkMap {

    default BukkitCallbackExecutor bridge$callbackExecutor() {
        return null;
    }
}
