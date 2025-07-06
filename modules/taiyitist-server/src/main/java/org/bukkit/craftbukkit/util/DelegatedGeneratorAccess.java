package org.bukkit.craftbukkit.util;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;

public abstract class DelegatedGeneratorAccess implements WorldGenLevel {
   private WorldGenLevel handle;

   public void setHandle(WorldGenLevel worldAccess) {
      this.handle = worldAccess;
   }

   public WorldGenLevel getHandle() {
      return this.handle;
   }

   public long getSeed() {
      return this.handle.getSeed();
   }

   public boolean ensureCanWrite(BlockPos blockposition) {
      return this.handle.ensureCanWrite(blockposition);
   }

   public void setCurrentlyGenerating(Supplier<String> supplier) {
      this.handle.setCurrentlyGenerating(supplier);
   }

   public ServerLevel getLevel() {
      return this.handle.getLevel();
   }

   public void addFreshEntityWithPassengers(Entity entity) {
      this.handle.addFreshEntityWithPassengers(entity);
   }

   public void addFreshEntityWithPassengers(Entity entity, CreatureSpawnEvent.SpawnReason reason) {
      this.handle.addFreshEntityWithPassengers(entity, reason);
   }

   public ServerLevel getMinecraftWorld() {
      return this.handle.getMinecraftWorld();
   }

   public long dayTime() {
      return this.handle.dayTime();
   }

   public long nextSubTickCount() {
      return this.handle.nextSubTickCount();
   }

   public <T> ScheduledTick<T> createTick(BlockPos blockposition, T t0, int i, TickPriority ticklistpriority) {
      return this.handle.createTick(blockposition, t0, i, ticklistpriority);
   }

   public <T> ScheduledTick<T> createTick(BlockPos blockposition, T t0, int i) {
      return this.handle.createTick(blockposition, t0, i);
   }

   public LevelData getLevelData() {
      return this.handle.getLevelData();
   }

   public DifficultyInstance getCurrentDifficultyAt(BlockPos blockposition) {
      return this.handle.getCurrentDifficultyAt(blockposition);
   }

   public MinecraftServer getServer() {
      return this.handle.getServer();
   }

   public Difficulty getDifficulty() {
      return this.handle.getDifficulty();
   }

   public ChunkSource getChunkSource() {
      return this.handle.getChunkSource();
   }

   public boolean hasChunk(int i, int j) {
      return this.handle.hasChunk(i, j);
   }

   public RandomSource getRandom() {
      return this.handle.getRandom();
   }

   public void updateNeighborsAt(BlockPos blockposition, Block block) {
      this.handle.updateNeighborsAt(blockposition, block);
   }

   public void neighborShapeChanged(Direction enumdirection, BlockPos blockposition, BlockPos blockposition1, BlockState iblockdata, int i, int j) {
      this.handle.neighborShapeChanged(enumdirection, blockposition, blockposition1, iblockdata, i, j);
   }

   public void playSound(Entity entity, BlockPos blockposition, SoundEvent soundeffect, SoundSource soundcategory) {
      this.handle.playSound(entity, blockposition, soundeffect, soundcategory);
   }

   public void playSound(Entity entity, BlockPos blockposition, SoundEvent soundeffect, SoundSource soundcategory, float f, float f1) {
      this.handle.playSound(entity, blockposition, soundeffect, soundcategory, f, f1);
   }

   public void addParticle(ParticleOptions particleparam, double d0, double d1, double d2, double d3, double d4, double d5) {
      this.handle.addParticle(particleparam, d0, d1, d2, d3, d4, d5);
   }

   public void levelEvent(Entity entity, int i, BlockPos blockposition, int j) {
      this.handle.levelEvent(entity, i, blockposition, j);
   }

   public void levelEvent(int i, BlockPos blockposition, int j) {
      this.handle.levelEvent(i, blockposition, j);
   }

   public void gameEvent(Holder<GameEvent> holder, Vec3 vec3d, GameEvent.Context gameevent_a) {
      this.handle.gameEvent(holder, vec3d, gameevent_a);
   }

   public void gameEvent(Entity entity, Holder<GameEvent> holder, Vec3 vec3d) {
      this.handle.gameEvent(entity, holder, vec3d);
   }

   public void gameEvent(Entity entity, Holder<GameEvent> holder, BlockPos blockposition) {
      this.handle.gameEvent(entity, holder, blockposition);
   }

   public void gameEvent(Holder<GameEvent> holder, BlockPos blockposition, GameEvent.Context gameevent_a) {
      this.handle.gameEvent(holder, blockposition, gameevent_a);
   }

   public void gameEvent(ResourceKey<GameEvent> resourcekey, BlockPos blockposition, GameEvent.Context gameevent_a) {
      this.handle.gameEvent(resourcekey, blockposition, gameevent_a);
   }

   public <T extends BlockEntity> Optional<T> getBlockEntity(BlockPos blockposition, BlockEntityType<T> tileentitytypes) {
      return this.handle.getBlockEntity(blockposition, tileentitytypes);
   }

   public List<VoxelShape> getEntityCollisions(Entity entity, AABB axisalignedbb) {
      return this.handle.getEntityCollisions(entity, axisalignedbb);
   }

   public boolean isUnobstructed(Entity entity, VoxelShape voxelshape) {
      return this.handle.isUnobstructed(entity, voxelshape);
   }

   public BlockPos getHeightmapPos(Heightmap.Types heightmap_type, BlockPos blockposition) {
      return this.handle.getHeightmapPos(heightmap_type, blockposition);
   }

   public float getMoonBrightness() {
      return this.handle.getMoonBrightness();
   }

   public float getTimeOfDay(float f) {
      return this.handle.getTimeOfDay(f);
   }

   public int getMoonPhase() {
      return this.handle.getMoonPhase();
   }

   public ChunkAccess getChunk(int i, int j, ChunkStatus chunkstatus, boolean flag) {
      return this.handle.getChunk(i, j, chunkstatus, flag);
   }

   public int getHeight(Heightmap.Types heightmap_type, int i, int j) {
      return this.handle.getHeight(heightmap_type, i, j);
   }

   public int getSkyDarken() {
      return this.handle.getSkyDarken();
   }

   public BiomeManager getBiomeManager() {
      return this.handle.getBiomeManager();
   }

   public Holder<Biome> getBiome(BlockPos blockposition) {
      return this.handle.getBiome(blockposition);
   }

   public Stream<BlockState> getBlockStatesIfLoaded(AABB axisalignedbb) {
      return this.handle.getBlockStatesIfLoaded(axisalignedbb);
   }

   public int getBlockTint(BlockPos blockposition, ColorResolver colorresolver) {
      return this.handle.getBlockTint(blockposition, colorresolver);
   }

   public Holder<Biome> getNoiseBiome(int i, int j, int k) {
      return this.handle.getNoiseBiome(i, j, k);
   }

   public Holder<Biome> getUncachedNoiseBiome(int i, int j, int k) {
      return this.handle.getUncachedNoiseBiome(i, j, k);
   }

   public boolean isClientSide() {
      return this.handle.isClientSide();
   }

   public int getSeaLevel() {
      return this.handle.getSeaLevel();
   }

   public DimensionType dimensionType() {
      return this.handle.dimensionType();
   }

   public int getMinY() {
      return this.handle.getMinY();
   }

   public int getHeight() {
      return this.handle.getHeight();
   }

   public boolean isEmptyBlock(BlockPos blockposition) {
      return this.handle.isEmptyBlock(blockposition);
   }

   public boolean canSeeSkyFromBelowWater(BlockPos blockposition) {
      return this.handle.canSeeSkyFromBelowWater(blockposition);
   }

   public float getPathfindingCostFromLightLevels(BlockPos blockposition) {
      return this.handle.getPathfindingCostFromLightLevels(blockposition);
   }

   public float getLightLevelDependentMagicValue(BlockPos blockposition) {
      return this.handle.getLightLevelDependentMagicValue(blockposition);
   }

   public ChunkAccess getChunk(BlockPos blockposition) {
      return this.handle.getChunk(blockposition);
   }

   public ChunkAccess getChunk(int i, int j) {
      return this.handle.getChunk(i, j);
   }

   public ChunkAccess getChunk(int i, int j, ChunkStatus chunkstatus) {
      return this.handle.getChunk(i, j, chunkstatus);
   }

   public BlockGetter getChunkForCollisions(int i, int j) {
      return this.handle.getChunkForCollisions(i, j);
   }

   public boolean isWaterAt(BlockPos blockposition) {
      return this.handle.isWaterAt(blockposition);
   }

   public boolean containsAnyLiquid(AABB axisalignedbb) {
      return this.handle.containsAnyLiquid(axisalignedbb);
   }

   public int getMaxLocalRawBrightness(BlockPos blockposition) {
      return this.handle.getMaxLocalRawBrightness(blockposition);
   }

   public int getMaxLocalRawBrightness(BlockPos blockposition, int i) {
      return this.handle.getMaxLocalRawBrightness(blockposition, i);
   }

   public boolean hasChunkAt(int i, int j) {
      return this.handle.hasChunkAt(i, j);
   }

   public boolean hasChunkAt(BlockPos blockposition) {
      return this.handle.hasChunkAt(blockposition);
   }

   public boolean hasChunksAt(BlockPos blockposition, BlockPos blockposition1) {
      return this.handle.hasChunksAt(blockposition, blockposition1);
   }

   public boolean hasChunksAt(int i, int j, int k, int l, int i1, int j1) {
      return this.handle.hasChunksAt(i, j, k, l, i1, j1);
   }

   public boolean hasChunksAt(int i, int j, int k, int l) {
      return this.handle.hasChunksAt(i, j, k, l);
   }

   public RegistryAccess registryAccess() {
      return this.handle.registryAccess();
   }

   public FeatureFlagSet enabledFeatures() {
      return this.handle.enabledFeatures();
   }

   public <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<? extends T>> resourcekey) {
      return this.handle.holderLookup(resourcekey);
   }

   public float getShade(Direction enumdirection, boolean flag) {
      return this.handle.getShade(enumdirection, flag);
   }

   public LevelLightEngine getLightEngine() {
      return this.handle.getLightEngine();
   }

   public int getBrightness(LightLayer enumskyblock, BlockPos blockposition) {
      return this.handle.getBrightness(enumskyblock, blockposition);
   }

   public int getRawBrightness(BlockPos blockposition, int i) {
      return this.handle.getRawBrightness(blockposition, i);
   }

   public boolean canSeeSky(BlockPos blockposition) {
      return this.handle.canSeeSky(blockposition);
   }

   public WorldBorder getWorldBorder() {
      return this.handle.getWorldBorder();
   }

   public boolean isUnobstructed(BlockState iblockdata, BlockPos blockposition, CollisionContext voxelshapecollision) {
      return this.handle.isUnobstructed(iblockdata, blockposition, voxelshapecollision);
   }

   public boolean isUnobstructed(Entity entity) {
      return this.handle.isUnobstructed(entity);
   }

   public boolean noCollision(AABB axisalignedbb) {
      return this.handle.noCollision(axisalignedbb);
   }

   public boolean noCollision(Entity entity) {
      return this.handle.noCollision(entity);
   }

   public boolean noCollision(Entity entity, AABB axisalignedbb) {
      return this.handle.noCollision(entity, axisalignedbb);
   }

   public boolean noCollision(Entity entity, AABB axisalignedbb, boolean flag) {
      return this.handle.noCollision(entity, axisalignedbb, flag);
   }

   public boolean noBlockCollision(Entity entity, AABB axisalignedbb) {
      return this.handle.noBlockCollision(entity, axisalignedbb);
   }

   public Iterable<VoxelShape> getCollisions(Entity entity, AABB axisalignedbb) {
      return this.handle.getCollisions(entity, axisalignedbb);
   }

   public Iterable<VoxelShape> getBlockCollisions(Entity entity, AABB axisalignedbb) {
      return this.handle.getBlockCollisions(entity, axisalignedbb);
   }

   public Iterable<VoxelShape> getBlockAndLiquidCollisions(Entity entity, AABB axisalignedbb) {
      return this.handle.getBlockAndLiquidCollisions(entity, axisalignedbb);
   }

   public BlockHitResult clipIncludingBorder(ClipContext raytrace) {
      return this.handle.clipIncludingBorder(raytrace);
   }

   public boolean collidesWithSuffocatingBlock(Entity entity, AABB axisalignedbb) {
      return this.handle.collidesWithSuffocatingBlock(entity, axisalignedbb);
   }

   public Optional<BlockPos> findSupportingBlock(Entity entity, AABB axisalignedbb) {
      return this.handle.findSupportingBlock(entity, axisalignedbb);
   }

   public Optional<Vec3> findFreePosition(Entity entity, VoxelShape voxelshape, Vec3 vec3d, double d0, double d1, double d2) {
      return this.handle.findFreePosition(entity, voxelshape, vec3d, d0, d1, d2);
   }

   public int getDirectSignal(BlockPos blockposition, Direction enumdirection) {
      return this.handle.getDirectSignal(blockposition, enumdirection);
   }

   public int getDirectSignalTo(BlockPos blockposition) {
      return this.handle.getDirectSignalTo(blockposition);
   }

   public int getControlInputSignal(BlockPos blockposition, Direction enumdirection, boolean flag) {
      return this.handle.getControlInputSignal(blockposition, enumdirection, flag);
   }

   public boolean hasSignal(BlockPos blockposition, Direction enumdirection) {
      return this.handle.hasSignal(blockposition, enumdirection);
   }

   public int getSignal(BlockPos blockposition, Direction enumdirection) {
      return this.handle.getSignal(blockposition, enumdirection);
   }

   public boolean hasNeighborSignal(BlockPos blockposition) {
      return this.handle.hasNeighborSignal(blockposition);
   }

   public int getBestNeighborSignal(BlockPos blockposition) {
      return this.handle.getBestNeighborSignal(blockposition);
   }

   public BlockEntity getBlockEntity(BlockPos blockposition) {
      return this.handle.getBlockEntity(blockposition);
   }

   public BlockState getBlockState(BlockPos blockposition) {
      return this.handle.getBlockState(blockposition);
   }

   public FluidState getFluidState(BlockPos blockposition) {
      return this.handle.getFluidState(blockposition);
   }

   public int getLightEmission(BlockPos blockposition) {
      return this.handle.getLightEmission(blockposition);
   }

   public Stream<BlockState> getBlockStates(AABB axisalignedbb) {
      return this.handle.getBlockStates(axisalignedbb);
   }

   public BlockHitResult isBlockInLine(ClipBlockStateContext clipblockstatecontext) {
      return this.handle.isBlockInLine(clipblockstatecontext);
   }

   public BlockHitResult clip(ClipContext raytrace1, BlockPos blockposition) {
      return this.handle.clip(raytrace1, blockposition);
   }

   public BlockHitResult clip(ClipContext raytrace) {
      return this.handle.clip(raytrace);
   }

   public BlockHitResult clipWithInteractionOverride(Vec3 vec3d, Vec3 vec3d1, BlockPos blockposition, VoxelShape voxelshape, BlockState iblockdata) {
      return this.handle.clipWithInteractionOverride(vec3d, vec3d1, blockposition, voxelshape, iblockdata);
   }

   public double getBlockFloorHeight(VoxelShape voxelshape, Supplier<VoxelShape> supplier) {
      return this.handle.getBlockFloorHeight(voxelshape, supplier);
   }

   public double getBlockFloorHeight(BlockPos blockposition) {
      return this.handle.getBlockFloorHeight(blockposition);
   }

   public List<Entity> getEntities(Entity entity, AABB axisalignedbb, Predicate<? super Entity> predicate) {
      return this.handle.getEntities(entity, axisalignedbb, predicate);
   }

   public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> entitytypetest, AABB axisalignedbb, Predicate<? super T> predicate) {
      return this.handle.getEntities(entitytypetest, axisalignedbb, predicate);
   }

   public <T extends Entity> List<T> getEntitiesOfClass(Class<T> oclass, AABB axisalignedbb, Predicate<? super T> predicate) {
      return this.handle.getEntitiesOfClass(oclass, axisalignedbb, predicate);
   }

   public List<? extends Player> players() {
      return this.handle.players();
   }

   public List<Entity> getEntities(Entity entity, AABB axisalignedbb) {
      return this.handle.getEntities(entity, axisalignedbb);
   }

   public <T extends Entity> List<T> getEntitiesOfClass(Class<T> oclass, AABB axisalignedbb) {
      return this.handle.getEntitiesOfClass(oclass, axisalignedbb);
   }

   public Player getNearestPlayer(double d0, double d1, double d2, double d3, Predicate<Entity> predicate) {
      return this.handle.getNearestPlayer(d0, d1, d2, d3, predicate);
   }

   public Player getNearestPlayer(Entity entity, double d0) {
      return this.handle.getNearestPlayer(entity, d0);
   }

   public Player getNearestPlayer(double d0, double d1, double d2, double d3, boolean flag) {
      return this.handle.getNearestPlayer(d0, d1, d2, d3, flag);
   }

   public boolean hasNearbyAlivePlayer(double d0, double d1, double d2, double d3) {
      return this.handle.hasNearbyAlivePlayer(d0, d1, d2, d3);
   }

   public Player getPlayerByUUID(UUID uuid) {
      return this.handle.getPlayerByUUID(uuid);
   }

   public boolean setBlock(BlockPos blockposition, BlockState iblockdata, int i, int j) {
      return this.handle.setBlock(blockposition, iblockdata, i, j);
   }

   public boolean setBlock(BlockPos blockposition, BlockState iblockdata, int i) {
      return this.handle.setBlock(blockposition, iblockdata, i);
   }

   public boolean removeBlock(BlockPos blockposition, boolean flag) {
      return this.handle.removeBlock(blockposition, flag);
   }

   public boolean destroyBlock(BlockPos blockposition, boolean flag) {
      return this.handle.destroyBlock(blockposition, flag);
   }

   public boolean destroyBlock(BlockPos blockposition, boolean flag, Entity entity) {
      return this.handle.destroyBlock(blockposition, flag, entity);
   }

   public boolean destroyBlock(BlockPos blockposition, boolean flag, Entity entity, int i) {
      return this.handle.destroyBlock(blockposition, flag, entity, i);
   }

   public boolean addFreshEntity(Entity entity) {
      return this.handle.addFreshEntity(entity);
   }

   public boolean addFreshEntity(Entity entity, CreatureSpawnEvent.SpawnReason reason) {
      return this.handle.addFreshEntity(entity, reason);
   }

   public int getMaxY() {
      return this.handle.getMaxY();
   }

   public int getSectionsCount() {
      return this.handle.getSectionsCount();
   }

   public int getMinSectionY() {
      return this.handle.getMinSectionY();
   }

   public int getMaxSectionY() {
      return this.handle.getMaxSectionY();
   }

   public boolean isInsideBuildHeight(int i) {
      return this.handle.isInsideBuildHeight(i);
   }

   public boolean isOutsideBuildHeight(BlockPos blockposition) {
      return this.handle.isOutsideBuildHeight(blockposition);
   }

   public boolean isOutsideBuildHeight(int i) {
      return this.handle.isOutsideBuildHeight(i);
   }

   public int getSectionIndex(int i) {
      return this.handle.getSectionIndex(i);
   }

   public int getSectionIndexFromSectionY(int i) {
      return this.handle.getSectionIndexFromSectionY(i);
   }

   public int getSectionYFromSectionIndex(int i) {
      return this.handle.getSectionYFromSectionIndex(i);
   }

   public LevelTickAccess<Block> getBlockTicks() {
      return this.handle.getBlockTicks();
   }

   public void scheduleTick(BlockPos blockposition, Block block, int i, TickPriority ticklistpriority) {
      this.handle.scheduleTick(blockposition, block, i, ticklistpriority);
   }

   public void scheduleTick(BlockPos blockposition, Block block, int i) {
      this.handle.scheduleTick(blockposition, block, i);
   }

   public LevelTickAccess<Fluid> getFluidTicks() {
      return this.handle.getFluidTicks();
   }

   public void scheduleTick(BlockPos blockposition, Fluid fluidtype, int i, TickPriority ticklistpriority) {
      this.handle.scheduleTick(blockposition, fluidtype, i, ticklistpriority);
   }

   public void scheduleTick(BlockPos blockposition, Fluid fluidtype, int i) {
      this.handle.scheduleTick(blockposition, fluidtype, i);
   }

   public boolean isStateAtPosition(BlockPos blockposition, Predicate<BlockState> predicate) {
      return this.handle.isStateAtPosition(blockposition, predicate);
   }

   public boolean isFluidAtPosition(BlockPos blockposition, Predicate<FluidState> predicate) {
      return this.handle.isFluidAtPosition(blockposition, predicate);
   }
}
