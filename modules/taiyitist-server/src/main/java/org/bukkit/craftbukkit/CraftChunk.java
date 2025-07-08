package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.ConsecutiveExecutor;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.chunk.PalettedContainer.Strategy;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.storage.EntityStorage;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.CraftBiome;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.GeneratedStructure;
import org.bukkit.generator.structure.Structure;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;

public class CraftChunk implements Chunk {
   private final ServerLevel worldServer;
   private final int x;
   private final int z;
   private static final PalettedContainer<BlockState> emptyBlockIDs;
   private static final byte[] FULL_LIGHT;
   private static final byte[] EMPTY_LIGHT;

   public CraftChunk(LevelChunk chunk) {
      this.worldServer = chunk.taiyitist$r();
      this.x = chunk.getPos().x;
      this.z = chunk.getPos().z;
   }

   public CraftChunk(ServerLevel worldServer, int x, int z) {
      this.worldServer = worldServer;
      this.x = x;
      this.z = z;
   }

   public World getWorld() {
      return this.worldServer.getWorld();
   }

   public CraftWorld getCraftWorld() {
      return (CraftWorld)this.getWorld();
   }

   public ChunkAccess getHandle(ChunkStatus chunkStatus) {
      ChunkAccess chunkAccess = this.worldServer.getChunk(this.x, this.z, chunkStatus);
      if (chunkAccess instanceof ImposterProtoChunk extension) {
         return extension.getWrapped();
      } else {
         return chunkAccess;
      }
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }

   public String toString() {
      int var10000 = this.getX();
      return "CraftChunk{x=" + var10000 + "z=" + this.getZ() + "}";
   }

   public Block getBlock(int x, int y, int z) {
      validateChunkCoordinates(this.worldServer.getMinY(), this.worldServer.getMaxY(), x, y, z);
      return new CraftBlock(this.worldServer, new BlockPos(this.x << 4 | x, y, this.z << 4 | z));
   }

   public boolean isEntitiesLoaded() {
      return this.getCraftWorld().getHandle().entityManager.areEntitiesLoaded(ChunkPos.asLong(this.x, this.z));
   }

   public Entity[] getEntities() {
      if (!this.isLoaded()) {
         this.getWorld().getChunkAt(this.x, this.z);
      }

      PersistentEntitySectionManager<net.minecraft.world.entity.Entity> entityManager = this.getCraftWorld().getHandle().entityManager;
      long pair = ChunkPos.asLong(this.x, this.z);
      if (entityManager.areEntitiesLoaded(pair)) {
         return (Entity[])entityManager.getEntities(new ChunkPos(this.x, this.z)).stream().map(net.minecraft.world.entity.Entity::getBukkitEntity).filter(Objects::nonNull).toArray((x$0) -> {
            return new Entity[x$0];
         });
      } else {
         entityManager.ensureChunkQueuedForLoad(pair);
         ConsecutiveExecutor mailbox = ((EntityStorage)entityManager.permanentStorage).entityDeserializerQueue;
         BooleanSupplier supplier = () -> {
            if (entityManager.areEntitiesLoaded(pair)) {
               return true;
            } else {
               if (!entityManager.isPending(pair)) {
                  entityManager.ensureChunkQueuedForLoad(pair);
               }

               entityManager.tick();
               return entityManager.areEntitiesLoaded(pair);
            }
         };

         while(!supplier.getAsBoolean()) {
            if (mailbox.size() != 0) {
               mailbox.run();
            } else {
               Thread.yield();
               LockSupport.parkNanos("waiting for entity loading", 100000L);
            }
         }

         return (Entity[])entityManager.getEntities(new ChunkPos(this.x, this.z)).stream().map(net.minecraft.world.entity.Entity::getBukkitEntity).filter(Objects::nonNull).toArray((x$0) -> {
            return new Entity[x$0];
         });
      }
   }

   public org.bukkit.block.BlockState[] getTileEntities() {
      if (!this.isLoaded()) {
         this.getWorld().getChunkAt(this.x, this.z);
      }

      int index = 0;
      ChunkAccess chunk = this.getHandle(ChunkStatus.FULL);
      org.bukkit.block.BlockState[] entities = new org.bukkit.block.BlockState[chunk.blockEntities.size()];

      BlockPos position;
      for(Iterator var4 = chunk.blockEntities.keySet().iterator(); var4.hasNext(); entities[index++] = this.worldServer.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ()).getState()) {
         position = (BlockPos)var4.next();
      }

      return entities;
   }

   public boolean isGenerated() {
      ChunkAccess chunk = this.getHandle(ChunkStatus.EMPTY);
      return chunk.getPersistedStatus().isOrAfter(ChunkStatus.FULL);
   }

   public boolean isLoaded() {
      return this.getWorld().isChunkLoaded(this);
   }

   public boolean load() {
      return this.getWorld().loadChunk(this.getX(), this.getZ(), true);
   }

   public boolean load(boolean generate) {
      return this.getWorld().loadChunk(this.getX(), this.getZ(), generate);
   }

   public boolean unload() {
      return this.getWorld().unloadChunk(this.getX(), this.getZ());
   }

   public boolean isSlimeChunk() {
      return WorldgenRandom.seedSlimeChunk(this.getX(), this.getZ(), this.getWorld().getSeed(), (long)this.worldServer.bridge$spigotConfig().slimeSeed).nextInt(10) == 0;
   }

   public boolean unload(boolean save) {
      return this.getWorld().unloadChunk(this.getX(), this.getZ(), save);
   }

   public boolean isForceLoaded() {
      return this.getWorld().isChunkForceLoaded(this.getX(), this.getZ());
   }

   public void setForceLoaded(boolean forced) {
      this.getWorld().setChunkForceLoaded(this.getX(), this.getZ(), forced);
   }

   public boolean addPluginChunkTicket(Plugin plugin) {
      return this.getWorld().addPluginChunkTicket(this.getX(), this.getZ(), plugin);
   }

   public boolean removePluginChunkTicket(Plugin plugin) {
      return this.getWorld().removePluginChunkTicket(this.getX(), this.getZ(), plugin);
   }

   public Collection<Plugin> getPluginChunkTickets() {
      return this.getWorld().getPluginChunkTickets(this.getX(), this.getZ());
   }

   public long getInhabitedTime() {
      return this.getHandle(ChunkStatus.EMPTY).getInhabitedTime();
   }

   public void setInhabitedTime(long ticks) {
      Preconditions.checkArgument(ticks >= 0L, "ticks cannot be negative");
      this.getHandle(ChunkStatus.STRUCTURE_STARTS).setInhabitedTime(ticks);
   }

   public boolean contains(BlockData block) {
      Preconditions.checkArgument(block != null, "Block cannot be null");
      Predicate<BlockState> nms = Predicates.equalTo(((CraftBlockData)block).getState());
      LevelChunkSection[] var3 = this.getHandle(ChunkStatus.FULL).getSections();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         LevelChunkSection section = var3[var5];
         if (section != null && section.getStates().maybeHas(nms)) {
            return true;
         }
      }

      return false;
   }

   public boolean contains(Biome biome) {
      Preconditions.checkArgument(biome != null, "Biome cannot be null");
      ChunkAccess chunk = this.getHandle(ChunkStatus.BIOMES);
      Predicate<Holder<net.minecraft.world.level.biome.Biome>> nms = Predicates.equalTo(CraftBiome.bukkitToMinecraftHolder(biome));
      LevelChunkSection[] var4 = chunk.getSections();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         LevelChunkSection section = var4[var6];
         if (section != null && section.getBiomes().maybeHas(nms)) {
            return true;
         }
      }

      return false;
   }

   public ChunkSnapshot getChunkSnapshot() {
      return this.getChunkSnapshot(true, false, false);
   }

   public ChunkSnapshot getChunkSnapshot(boolean includeMaxBlockY, boolean includeBiome, boolean includeBiomeTempRain) {
      ChunkAccess chunk = this.getHandle(ChunkStatus.FULL);
      LevelChunkSection[] cs = chunk.getSections();
      PalettedContainer[] sectionBlockIDs = new PalettedContainer[cs.length];
      byte[][] sectionSkyLights = new byte[cs.length][];
      byte[][] sectionEmitLights = new byte[cs.length][];
      boolean[] sectionEmpty = new boolean[cs.length];
      PalettedContainerRO<Holder<net.minecraft.world.level.biome.Biome>>[] biome = !includeBiome && !includeBiomeTempRain ? null : new PalettedContainer[cs.length];
      Registry<net.minecraft.world.level.biome.Biome> iregistry = this.worldServer.registryAccess().lookupOrThrow(Registries.BIOME);
      Codec<PalettedContainerRO<Holder<net.minecraft.world.level.biome.Biome>>> biomeCodec = PalettedContainer.codecRO(iregistry.asHolderIdMap(), iregistry.holderByNameCodec(), Strategy.SECTION_BIOMES, iregistry.getOrThrow(Biomes.PLAINS));

      for(int i = 0; i < cs.length; ++i) {
         CompoundTag data = new CompoundTag();
         data.put("block_states", (Tag)SerializableChunkData.BLOCK_STATE_CODEC.encodeStart(NbtOps.INSTANCE, cs[i].getStates()).getOrThrow());
         sectionBlockIDs[i] = (PalettedContainer)SerializableChunkData.BLOCK_STATE_CODEC.parse(NbtOps.INSTANCE, data.getCompoundOrEmpty("block_states")).getOrThrow(SerializableChunkData.ChunkReadException::new);
         sectionEmpty[i] = cs[i].hasOnlyAir();
         LevelLightEngine lightengine = this.worldServer.getLightEngine();
         DataLayer skyLightArray = lightengine.getLayerListener(LightLayer.SKY).getDataLayerData(SectionPos.of(this.x, chunk.getSectionYFromSectionIndex(i), this.z));
         if (skyLightArray == null) {
            sectionSkyLights[i] = this.worldServer.dimensionType().hasSkyLight() ? FULL_LIGHT : EMPTY_LIGHT;
         } else {
            sectionSkyLights[i] = new byte[2048];
            System.arraycopy(skyLightArray.getData(), 0, sectionSkyLights[i], 0, 2048);
         }

         DataLayer emitLightArray = lightengine.getLayerListener(LightLayer.BLOCK).getDataLayerData(SectionPos.of(this.x, chunk.getSectionYFromSectionIndex(i), this.z));
         if (emitLightArray == null) {
            sectionEmitLights[i] = EMPTY_LIGHT;
         } else {
            sectionEmitLights[i] = new byte[2048];
            System.arraycopy(emitLightArray.getData(), 0, sectionEmitLights[i], 0, 2048);
         }

         if (biome != null) {
            data.put("biomes", (Tag)biomeCodec.encodeStart(NbtOps.INSTANCE, cs[i].getBiomes()).getOrThrow());
            biome[i] = (PalettedContainerRO)biomeCodec.parse(NbtOps.INSTANCE, data.getCompoundOrEmpty("biomes")).getOrThrow(SerializableChunkData.ChunkReadException::new);
         }
      }

      Heightmap hmap = null;
      if (includeMaxBlockY) {
         hmap = new Heightmap(chunk, Types.MOTION_BLOCKING);
         hmap.setRawData(chunk, Types.MOTION_BLOCKING, ((Heightmap)chunk.heightmaps.get(Types.MOTION_BLOCKING)).getRawData());
      }

      World world = this.getWorld();
      return new CraftChunkSnapshot(this.getX(), this.getZ(), chunk.getMinY(), chunk.getMaxY(), world.getSeaLevel(), world.getName(), world.getFullTime(), sectionBlockIDs, sectionSkyLights, sectionEmitLights, sectionEmpty, hmap, iregistry, biome);
   }

   public PersistentDataContainer getPersistentDataContainer() {
      return this.getHandle(ChunkStatus.STRUCTURE_STARTS).bridge$persistentDataContainer();
   }

   public Chunk.LoadLevel getLoadLevel() {
      LevelChunk chunk = this.worldServer.getChunkIfLoaded(this.getX(), this.getZ());
      return chunk == null ? LoadLevel.UNLOADED : LoadLevel.values()[chunk.getFullStatus().ordinal()];
   }

   public Collection<GeneratedStructure> getStructures() {
      return this.getCraftWorld().getStructures(this.getX(), this.getZ());
   }

   public Collection<GeneratedStructure> getStructures(Structure structure) {
      return this.getCraftWorld().getStructures(this.getX(), this.getZ(), structure);
   }

   public Collection<Player> getPlayersSeeingChunk() {
      return this.getWorld().getPlayersSeeingChunk(this);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         CraftChunk that = (CraftChunk)o;
         if (this.x != that.x) {
            return false;
         } else {
            return this.z != that.z ? false : this.worldServer.equals(that.worldServer);
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.worldServer.hashCode();
      result = 31 * result + this.x;
      result = 31 * result + this.z;
      return result;
   }

   public static ChunkSnapshot getEmptyChunkSnapshot(int x, int z, CraftWorld world, boolean includeBiome, boolean includeBiomeTempRain) {
      ChunkAccess actual = world.getHandle().getChunk(x, z, !includeBiome && !includeBiomeTempRain ? ChunkStatus.EMPTY : ChunkStatus.BIOMES);
      int hSection = actual.getSectionsCount();
      PalettedContainer[] blockIDs = new PalettedContainer[hSection];
      byte[][] skyLight = new byte[hSection][];
      byte[][] emitLight = new byte[hSection][];
      boolean[] empty = new boolean[hSection];
      Registry<net.minecraft.world.level.biome.Biome> iregistry = world.getHandle().registryAccess().lookupOrThrow(Registries.BIOME);
      PalettedContainer<Holder<net.minecraft.world.level.biome.Biome>>[] biome = !includeBiome && !includeBiomeTempRain ? null : new PalettedContainer[hSection];
      Codec<PalettedContainerRO<Holder<net.minecraft.world.level.biome.Biome>>> biomeCodec = PalettedContainer.codecRO(iregistry.asHolderIdMap(), iregistry.holderByNameCodec(), Strategy.SECTION_BIOMES, iregistry.getOrThrow(Biomes.PLAINS));

      for(int i = 0; i < hSection; ++i) {
         blockIDs[i] = emptyBlockIDs;
         skyLight[i] = world.getHandle().dimensionType().hasSkyLight() ? FULL_LIGHT : EMPTY_LIGHT;
         emitLight[i] = EMPTY_LIGHT;
         empty[i] = true;
         if (biome != null) {
            biome[i] = (PalettedContainer)biomeCodec.parse(NbtOps.INSTANCE, (Tag)biomeCodec.encodeStart(NbtOps.INSTANCE, actual.getSection(i).getBiomes()).getOrThrow()).getOrThrow(SerializableChunkData.ChunkReadException::new);
         }
      }

      return new CraftChunkSnapshot(x, z, world.getMinHeight(), world.getMaxHeight(), world.getSeaLevel(), world.getName(), world.getFullTime(), blockIDs, skyLight, emitLight, empty, new Heightmap(actual, Types.MOTION_BLOCKING), iregistry, biome);
   }

   static void validateChunkCoordinates(int minY, int maxY, int x, int y, int z) {
      Preconditions.checkArgument(0 <= x && x <= 15, "x out of range (expected 0-15, got %s)", x);
      Preconditions.checkArgument(minY <= y && y <= maxY, "y out of range (expected %s-%s, got %s)", minY, maxY, y);
      Preconditions.checkArgument(0 <= z && z <= 15, "z out of range (expected 0-15, got %s)", z);
   }

   static {
      emptyBlockIDs = new PalettedContainer(net.minecraft.world.level.block.Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), Strategy.SECTION_STATES);
      FULL_LIGHT = new byte[2048];
      EMPTY_LIGHT = new byte[2048];
      Arrays.fill(FULL_LIGHT, (byte)-1);
   }
}
