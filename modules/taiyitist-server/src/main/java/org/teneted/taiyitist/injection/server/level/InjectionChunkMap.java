package org.teneted.taiyitist.injection.server.level;

import org.teneted.taiyitist.bukkit.BukkitCallbackExecutor;

public interface InjectionChunkMap {

    default BukkitCallbackExecutor bridge$callbackExecutor() {
        return null;
    }
}
