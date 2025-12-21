package org.celestial_artistry.taiyitist.injection.world.level.storage;

import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.LevelStem;

public interface InjectionPrimaryLevelData {

    default Registry<LevelStem> bridge$customDimensions() {
        return null;
    }

    default void taiyitist$setCustomDimensions(Registry<LevelStem> customDimensions) {
    }

    default void checkName(String name) {
    }

    default void setWorld(ServerLevel world) {
    }
}
