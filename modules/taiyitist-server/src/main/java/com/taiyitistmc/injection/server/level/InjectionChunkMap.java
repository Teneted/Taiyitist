package com.taiyitistmc.injection.server.level;

import com.taiyitistmc.bukkit.BukkitCallbackExecutor;

public interface InjectionChunkMap {

    default BukkitCallbackExecutor bridge$callbackExecutor() {
        throw new IllegalStateException("Not implemented");
    }
}
