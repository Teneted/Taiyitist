package org.teneted.taiyitist.injection.world.level.chunk.storage;

import java.io.IOException;
import net.minecraft.world.level.ChunkPos;

public interface InjectionRegionFileStorage {

    default boolean chunkExists(ChunkPos pos) throws IOException {
        throw new IllegalStateException("Not implemented");
    }
}
