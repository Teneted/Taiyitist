package org.teneted.taiyitist.injection.world.level.levelgen;

import net.minecraft.world.level.biome.BiomeSource;

public interface InjectionFlatLevelSource {

    default void taiyitist$setBiomeSource(BiomeSource biomeSource) {
    }
}
