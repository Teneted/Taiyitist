package org.teneted.taiyitist.injection.world.level.entity;

import java.io.IOException;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;

public interface InjectionPersistentEntitySectionManager {

    default List<Entity> getEntities(ChunkPos chunkCoordIntPair) {
        return null;
    }

    default boolean isPending(long pair) {
        return false;
    }

    default void close(boolean save) throws IOException {
    }
}
