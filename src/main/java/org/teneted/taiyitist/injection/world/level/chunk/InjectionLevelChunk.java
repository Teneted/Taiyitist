package org.teneted.taiyitist.injection.world.level.chunk;

import net.minecraft.server.level.ServerLevel;

public interface InjectionLevelChunk {

    default org.bukkit.Chunk getBukkitChunk() {
        throw new IllegalStateException("Not implemented");
    }

    default ServerLevel taiyitist$r() {
        throw new IllegalStateException("Not implemented");
    }

    default void loadCallback() {
        throw new IllegalStateException("Not implemented");
    }

    default void unloadCallback() {
        throw new IllegalStateException("Not implemented");
    }
}
