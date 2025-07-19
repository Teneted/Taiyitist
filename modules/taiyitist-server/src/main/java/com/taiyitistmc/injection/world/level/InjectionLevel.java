package com.taiyitistmc.injection.world.level;

import com.taiyitistmc.config.TaiyitistWorldConfig;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.LevelStem;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CapturedBlockState;
import org.bukkit.entity.SpawnCategory;
import org.spigotmc.SpigotWorldConfig;

public interface InjectionLevel {

    default boolean bridge$preventPoiUpdated() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setPreventPoiUpdated(boolean preventPoiUpdated) {
        throw new IllegalStateException("Not implemented");
    }

    default org.bukkit.generator.BiomeProvider bridge$biomeProvider() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setBiomeProvider(org.bukkit.generator.BiomeProvider biomeProvider) {
        throw new IllegalStateException("Not implemented");
    }

    default org.bukkit.World.Environment bridge$environment() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setEnvironment(org.bukkit.World.Environment environment) {
        throw new IllegalStateException("Not implemented");
    }

    default org.bukkit.generator.ChunkGenerator bridge$generator() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setGenerator(org.bukkit.generator.ChunkGenerator generator) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean bridge$pvpMode() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setPvpMode(boolean pvpMode) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean bridge$captureBlockStates() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setCaptureBlockStates(boolean captureState) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean bridge$captureTreeGeneration() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setCaptureTreeGeneration(boolean treeGeneration) {
        throw new IllegalStateException("Not implemented");
    }

    default Map<BlockPos, CapturedBlockState> bridge$capturedBlockStates() {
        throw new IllegalStateException("Not implemented");
    }

    default Map<BlockPos, BlockEntity> bridge$capturedTileEntities() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setCapturedTileEntities(Map<BlockPos, BlockEntity> tileEntities) {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setCapturedBlockStates(Map<BlockPos, CapturedBlockState> capturedBlockStates) {
        throw new IllegalStateException("Not implemented");
    }

    default List<ItemEntity> bridge$captureDrops() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setCaptureDrops(List<ItemEntity> captureDrops) {
        throw new IllegalStateException("Not implemented");
    }

    default it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap<SpawnCategory> bridge$ticksPerSpawnCategory() {
        throw new IllegalStateException("Not implemented");
    }

    default boolean bridge$populating() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setPopulating(boolean populating) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean bridge$keepSpawnInMemory() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setKeepSpawnInMemory(boolean keepSpawnInMemory) {
        throw new IllegalStateException("Not implemented");
    }

    default ResourceKey<LevelStem> getTypeKey(){
        throw new IllegalStateException("Not implemented");
    }

    default CraftWorld getWorld() {
        throw new IllegalStateException("Not implemented");
    }

    default CraftServer getCraftServer() {
        throw new IllegalStateException("Not implemented");
    }

    default void notifyAndUpdatePhysics(BlockPos blockposition, LevelChunk chunk, BlockState oldBlock, BlockState newBlock, BlockState actualBlock, int i, int j) {
        throw new IllegalStateException("Not implemented");
    }

    default BlockEntity getBlockEntity(BlockPos blockposition, boolean validate) {
        throw new IllegalStateException("Not implemented");
    }

    default SpigotWorldConfig bridge$spigotConfig() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setSpigotConfig(SpigotWorldConfig spigotWorldConfig) {
        throw new IllegalStateException("Not implemented");
    }

    default TaiyitistWorldConfig bridge$bannerConfig() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setBannerConfig(TaiyitistWorldConfig taiyitistWorldConfig) {
        throw new IllegalStateException("Not implemented");
    }

    default CraftWorld taiyitist$initWorld(LevelStem levelStem) {
        throw new IllegalStateException("Not implemented");
    }
}
