package org.teneted.taiyitist.injection.world.level.chunk.storage;

import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public interface InjectionChunkStorage {

    default CompoundTag upgradeChunkTag(ResourceKey<LevelStem> resourceKey, Supplier<DimensionDataStorage> supplier, CompoundTag compoundTag, Optional<ResourceKey<MapCodec<? extends ChunkGenerator>>> optional, ChunkPos pos, @Nullable LevelAccessor generatoraccess) {
        throw new IllegalStateException("Not implemented");
    }

    default void pushChunkPos(ChunkPos pos) {
        throw new IllegalStateException("Not implemented");
    }

    default void pushLevelAccessor(LevelAccessor generatoraccess) {
        throw new IllegalStateException("Not implemented");
    }
}
