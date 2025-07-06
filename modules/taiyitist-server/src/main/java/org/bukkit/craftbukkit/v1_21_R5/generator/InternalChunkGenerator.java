package org.bukkit.craftbukkit.v1_21_R5.generator;

import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;

public abstract class InternalChunkGenerator extends ChunkGenerator {
   public InternalChunkGenerator(BiomeSource worldchunkmanager, Function<Holder<Biome>, BiomeGenerationSettings> function) {
      super(worldchunkmanager, function);
   }
}
