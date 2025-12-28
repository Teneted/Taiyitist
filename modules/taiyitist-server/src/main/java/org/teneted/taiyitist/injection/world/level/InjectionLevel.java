package org.teneted.taiyitist.injection.world.level;

import org.teneted.taiyitist.config.TaiyitistWorldConfig;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.LevelStem;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.block.CapturedBlockState;
import org.bukkit.entity.SpawnCategory;
import org.spigotmc.SpigotWorldConfig;

public interface InjectionLevel {

    default boolean bridge$preventPoiUpdated() {
        return false;
    }

    default void taiyitist$setPreventPoiUpdated(boolean preventPoiUpdated) {
    }

    default org.bukkit.generator.BiomeProvider bridge$biomeProvider() {
        return null;
    }

    default void taiyitist$setBiomeProvider(org.bukkit.generator.BiomeProvider biomeProvider) {
    }

    default org.bukkit.World.Environment bridge$environment() {
        return null;
    }

    default void taiyitist$setEnvironment(org.bukkit.World.Environment environment) {
    }

    default org.bukkit.generator.ChunkGenerator bridge$generator() {
        return null;
    }

    default void taiyitist$setGenerator(org.bukkit.generator.ChunkGenerator generator) {
    }

    default boolean bridge$pvpMode() {
        return false;
    }

    default void taiyitist$setPvpMode(boolean pvpMode) {
    }

    default boolean bridge$captureBlockStates() {
        return false;
    }

    default void taiyitist$setCaptureBlockStates(boolean captureState) {
    }

    default boolean bridge$captureTreeGeneration() {
        return false;
    }

    default void taiyitist$setCaptureTreeGeneration(boolean treeGeneration) {
    }

    default Map<BlockPos, CapturedBlockState> bridge$capturedBlockStates() {
        return null;
    }

    default Map<BlockPos, BlockEntity> bridge$capturedTileEntities() {
        return null;
    }

    default void taiyitist$setCapturedTileEntities(Map<BlockPos, BlockEntity> tileEntities) {
    }

    default void taiyitist$setCapturedBlockStates(Map<BlockPos, CapturedBlockState> capturedBlockStates) {
    }

    default List<ItemEntity> bridge$captureDrops() {
        return null;
    }

    default void taiyitist$setCaptureDrops(List<ItemEntity> captureDrops) {

    }

    default it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap<SpawnCategory> bridge$ticksPerSpawnCategory() {
        return null;
    }

    default boolean bridge$populating() {
        return false;
    }

    default void taiyitist$setPopulating(boolean populating) {

    }

    default boolean bridge$KeepSpawnInMemory() {
        return false;
    }

    default void taiyitist$setKeepSpawnInMemory(boolean keepSpawnInMemory) {
    }

    default ResourceKey<LevelStem> getTypeKey(){
        return null;
    }

    default CraftWorld getWorld() {
        return null;
    }

    default CraftServer getCraftServer() {
        return null;
    }

    default void notifyAndUpdatePhysics(BlockPos blockposition, LevelChunk chunk, BlockState oldBlock, BlockState newBlock, BlockState actualBlock, int i, int j) {
    }

    default BlockEntity getBlockEntity(BlockPos blockposition, boolean validate) {
        return null;
    }

    default SpigotWorldConfig bridge$spigotConfig() {
        return null;
    }

    default void taiyitist$setSpigotConfig(SpigotWorldConfig spigotWorldConfig) {
    }

    default TaiyitistWorldConfig bridge$bannerConfig() {
        return null;
    }

    default void taiyitist$setBannerConfig(TaiyitistWorldConfig taiyitistWorldConfig) {
    }

    default void taiyitist$callEvent(boolean call) {
    }

    default BlockState taiyitist$defaultBlockState() {
        return null;
    }
}
