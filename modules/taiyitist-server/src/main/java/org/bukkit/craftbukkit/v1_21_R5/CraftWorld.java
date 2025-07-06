package org.bukkit.craftbukkit.v1_21_R5;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.Ticket;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.TicketStorage;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.bukkit.BlockChangeDelegate;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Difficulty;
import org.bukkit.Effect;
import org.bukkit.FeatureFlag;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameRule;
import org.bukkit.HeightMap;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Note;
import org.bukkit.Particle;
import org.bukkit.Raid;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.StructureType;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.DragonBattle;
import org.bukkit.craftbukkit.v1_21_R5.block.CraftBiome;
import org.bukkit.craftbukkit.v1_21_R5.block.CraftBlock;
import org.bukkit.craftbukkit.v1_21_R5.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_21_R5.block.CraftBlockType;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.boss.CraftDragonBattle;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R5.generator.structure.CraftGeneratedStructure;
import org.bukkit.craftbukkit.v1_21_R5.generator.structure.CraftStructure;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R5.metadata.BlockMetadataStore;
import org.bukkit.craftbukkit.v1_21_R5.persistence.CraftPersistentDataContainer;
import org.bukkit.craftbukkit.v1_21_R5.persistence.CraftPersistentDataTypeRegistry;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftBiomeSearchResult;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftLocation;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftRayTraceResult;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftSpawnCategory;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftStructureSearchResult;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.TippedArrow;
import org.bukkit.entity.Trident;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.weather.LightningStrikeEvent.Cause;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.event.world.TimeSkipEvent.SkipReason;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.structure.GeneratedStructure;
import org.bukkit.generator.structure.Structure;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.potion.PotionType;
import org.bukkit.util.BiomeSearchResult;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.StructureSearchResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spigotmc.AsyncCatcher;

public class CraftWorld extends CraftRegionAccessor implements World {
   public static final int CUSTOM_DIMENSION_OFFSET = 10;
   private static final CraftPersistentDataTypeRegistry DATA_TYPE_REGISTRY = new CraftPersistentDataTypeRegistry();
   private final ServerLevel world;
   private WorldBorder worldBorder;
   private World.Environment environment;
   private final CraftServer server = (CraftServer)Bukkit.getServer();
   private final ChunkGenerator generator;
   private final BiomeProvider biomeProvider;
   private final List<BlockPopulator> populators = new ArrayList();
   private final BlockMetadataStore blockMetadata = new BlockMetadataStore(this);
   private final Object2IntOpenHashMap<SpawnCategory> spawnCategoryLimit = new Object2IntOpenHashMap();
   private final CraftPersistentDataContainer persistentDataContainer;
   private static final Random rand = new Random();
   private Map<String, GameRules.Key<?>> gamerules;
   private Map<String, GameRules.Type<?>> gameruleDefinitions;
   private final World.Spigot spigot;

   public CraftWorld(ServerLevel world, ChunkGenerator gen, BiomeProvider biomeProvider, World.Environment env) {
      this.persistentDataContainer = new CraftPersistentDataContainer(DATA_TYPE_REGISTRY);
      this.spigot = new World.Spigot() {
         public LightningStrike strikeLightning(Location loc, boolean isSilent) {
            return CraftWorld.this.strikeLightning(loc);
         }

         public LightningStrike strikeLightningEffect(Location loc, boolean isSilent) {
            return CraftWorld.this.strikeLightningEffect(loc);
         }
      };
      this.world = world;
      this.generator = gen;
      this.biomeProvider = biomeProvider;
      this.environment = env;
   }

   public Block getBlockAt(int x, int y, int z) {
      return CraftBlock.at(this.world, new BlockPos(x, y, z));
   }

   public Location getSpawnLocation() {
      BlockPos spawn = this.world.getSharedSpawnPos();
      float yaw = this.world.getSharedSpawnAngle();
      return CraftLocation.toBukkit((BlockPos)spawn, this, yaw, 0.0F);
   }

   public boolean setSpawnLocation(Location location) {
      Preconditions.checkArgument(location != null, "location");
      return this.equals(location.getWorld()) ? this.setSpawnLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getYaw()) : false;
   }

   public boolean setSpawnLocation(int x, int y, int z, float angle) {
      try {
         this.world.setDefaultSpawnPos(new BlockPos(x, y, z), angle);
         return true;
      } catch (Exception var6) {
         return false;
      }
   }

   public boolean setSpawnLocation(int x, int y, int z) {
      return this.setSpawnLocation(x, y, z, 0.0F);
   }

   public Chunk getChunkAt(int x, int z) {
      LevelChunk chunk = (LevelChunk)this.world.getChunk(x, z, ChunkStatus.FULL, true);
      return new CraftChunk(chunk);
   }

   @NotNull
   public Chunk getChunkAt(int x, int z, boolean generate) {
      return (Chunk)(generate ? this.getChunkAt(x, z) : new CraftChunk(this.getHandle(), x, z));
   }

   public Chunk getChunkAt(Block block) {
      Preconditions.checkArgument(block != null, "null block");
      return this.getChunkAt(block.getX() >> 4, block.getZ() >> 4);
   }

   public boolean isChunkLoaded(int x, int z) {
      return this.world.getChunkSource().isChunkLoaded(x, z);
   }

   public boolean isChunkGenerated(int x, int z) {
      try {
         return this.isChunkLoaded(x, z) || ((Optional)this.world.getChunkSource().chunkMap.read(new ChunkPos(x, z)).get()).isPresent();
      } catch (ExecutionException | InterruptedException var4) {
         Exception ex = var4;
         throw new RuntimeException(ex);
      }
   }

   public Chunk[] getLoadedChunks() {
      Long2ObjectLinkedOpenHashMap<ChunkHolder> chunks = this.world.getChunkSource().chunkMap.visibleChunkMap;
      return (Chunk[])chunks.values().stream().map(ChunkHolder::getFullChunkNow).filter(Objects::nonNull).map(CraftChunk::new).toArray((x$0) -> {
         return new Chunk[x$0];
      });
   }

   public void loadChunk(int x, int z) {
      this.loadChunk(x, z, true);
   }

   public boolean unloadChunk(Chunk chunk) {
      return this.unloadChunk(chunk.getX(), chunk.getZ());
   }

   public boolean unloadChunk(int x, int z) {
      return this.unloadChunk(x, z, true);
   }

   public boolean unloadChunk(int x, int z, boolean save) {
      return this.unloadChunk0(x, z, save);
   }

   public boolean unloadChunkRequest(int x, int z) {
      AsyncCatcher.catchOp("chunk unload");
      if (this.isChunkLoaded(x, z)) {
         this.world.getChunkSource().removeTicketWithRadius(TicketType.PLUGIN, new ChunkPos(x, z), 1);
      }

      return true;
   }

   private boolean unloadChunk0(int x, int z, boolean save) {
      AsyncCatcher.catchOp("chunk unload");
      if (!this.isChunkLoaded(x, z)) {
         return true;
      } else {
         LevelChunk chunk = this.world.getChunk(x, z);
         if (!save) {
            chunk.tryMarkSaved();
         }

         this.unloadChunkRequest(x, z);
         this.world.getChunkSource().purgeUnload();
         return !this.isChunkLoaded(x, z);
      }
   }

   public boolean regenerateChunk(int x, int z) {
      AsyncCatcher.catchOp("chunk regenerate");
      throw new UnsupportedOperationException("Not supported in this Minecraft version! Unless you can fix it, this is not a bug :)");
   }

   public boolean refreshChunk(int x, int z) {
      ChunkHolder playerChunk = (ChunkHolder)this.world.getChunkSource().chunkMap.visibleChunkMap.get(ChunkPos.asLong(x, z));
      if (playerChunk == null) {
         return false;
      } else {
         playerChunk.getTickingChunkFuture().thenAccept((either) -> {
            either.ifSuccess((chunk) -> {
               List<ServerPlayer> playersInRange = playerChunk.playerProvider.getPlayers(playerChunk.getPos(), false);
               if (!playersInRange.isEmpty()) {
                  ClientboundLevelChunkWithLightPacket refreshPacket = new ClientboundLevelChunkWithLightPacket(chunk, this.world.getLightEngine(), (BitSet)null, (BitSet)null);
                  Iterator var5 = playersInRange.iterator();

                  while(var5.hasNext()) {
                     ServerPlayer player = (ServerPlayer)var5.next();
                     if (player.connection != null) {
                        player.connection.send(refreshPacket);
                     }
                  }

               }
            });
         });
         return true;
      }
   }

   public Collection<Player> getPlayersSeeingChunk(Chunk chunk) {
      Preconditions.checkArgument(chunk != null, "chunk cannot be null");
      return this.getPlayersSeeingChunk(chunk.getX(), chunk.getZ());
   }

   public Collection<Player> getPlayersSeeingChunk(int x, int z) {
      if (!this.isChunkLoaded(x, z)) {
         return Collections.emptySet();
      } else {
         List<ServerPlayer> players = this.world.getChunkSource().chunkMap.getPlayers(new ChunkPos(x, z), false);
         return (Collection)(players.isEmpty() ? Collections.emptySet() : (Collection)players.stream().filter(Objects::nonNull).map(ServerPlayer::getBukkitEntity).collect(Collectors.toUnmodifiableSet()));
      }
   }

   public boolean isChunkInUse(int x, int z) {
      return this.isChunkLoaded(x, z);
   }

   public boolean loadChunk(int x, int z, boolean generate) {
      AsyncCatcher.catchOp("chunk load");
      ChunkAccess chunk = this.world.getChunkSource().getChunk(x, z, generate ? ChunkStatus.FULL : ChunkStatus.EMPTY, true);
      if (chunk instanceof ImposterProtoChunk) {
         chunk = this.world.getChunkSource().getChunk(x, z, ChunkStatus.FULL, true);
      }

      if (chunk instanceof LevelChunk) {
         this.world.getChunkSource().addTicketWithRadius(TicketType.PLUGIN, new ChunkPos(x, z), 1);
         return true;
      } else {
         return false;
      }
   }

   public boolean isChunkLoaded(Chunk chunk) {
      Preconditions.checkArgument(chunk != null, "null chunk");
      return this.isChunkLoaded(chunk.getX(), chunk.getZ());
   }

   public void loadChunk(Chunk chunk) {
      Preconditions.checkArgument(chunk != null, "null chunk");
      this.loadChunk(chunk.getX(), chunk.getZ());
   }

   public boolean addPluginChunkTicket(int x, int z, Plugin plugin) {
      Preconditions.checkArgument(plugin != null, "null plugin");
      Preconditions.checkArgument(plugin.isEnabled(), "plugin is not enabled");
      TicketStorage chunkDistanceManager = this.world.getChunkSource().ticketStorage;
      if (chunkDistanceManager.addTicket(ChunkPos.asLong(x, z), Ticket.of(TicketType.PLUGIN_TICKET, ChunkMap.FORCED_TICKET_LEVEL, plugin))) {
         this.getChunkAt(x, z);
         return true;
      } else {
         return false;
      }
   }

   public boolean removePluginChunkTicket(int x, int z, Plugin plugin) {
      Preconditions.checkNotNull(plugin, "null plugin");
      TicketStorage chunkDistanceManager = this.world.getChunkSource().ticketStorage;
      return chunkDistanceManager.removeTicket(ChunkPos.asLong(x, z), Ticket.of(TicketType.PLUGIN_TICKET, ChunkMap.FORCED_TICKET_LEVEL, plugin));
   }

   public void removePluginChunkTickets(Plugin plugin) {
      Preconditions.checkNotNull(plugin, "null plugin");
      TicketStorage chunkDistanceManager = this.world.getChunkSource().ticketStorage;
      chunkDistanceManager.removeTicketIf((pos, ticket) -> {
         return ticket.getType() == TicketType.PLUGIN_TICKET && Objects.equals(ticket.key, plugin);
      }, (Long2ObjectOpenHashMap)null);
   }

   public Collection<Plugin> getPluginChunkTickets(int x, int z) {
      TicketStorage chunkDistanceManager = this.world.getChunkSource().ticketStorage;
      List<Ticket> tickets = chunkDistanceManager.getTickets(ChunkPos.asLong(x, z));
      if (tickets == null) {
         return Collections.emptyList();
      } else {
         ImmutableList.Builder<Plugin> ret = ImmutableList.builder();
         Iterator var6 = tickets.iterator();

         while(var6.hasNext()) {
            Ticket ticket = (Ticket)var6.next();
            if (ticket.getType() == TicketType.PLUGIN_TICKET) {
               ret.add((Plugin)ticket.key);
            }
         }

         return ret.build();
      }
   }

   public Map<Plugin, Collection<Chunk>> getPluginChunkTickets() {
      Map<Plugin, ImmutableList.Builder<Chunk>> ret = new HashMap();
      TicketStorage chunkDistanceManager = this.world.getChunkSource().ticketStorage;
      ObjectIterator var3 = chunkDistanceManager.tickets.long2ObjectEntrySet().iterator();

      while(var3.hasNext()) {
         Long2ObjectMap.Entry<List<Ticket>> chunkTickets = (Long2ObjectMap.Entry)var3.next();
         long chunkKey = chunkTickets.getLongKey();
         List<Ticket> tickets = (List)chunkTickets.getValue();
         Chunk chunk = null;
         Iterator var9 = tickets.iterator();

         while(var9.hasNext()) {
            Ticket ticket = (Ticket)var9.next();
            if (ticket.getType() == TicketType.PLUGIN_TICKET) {
               if (chunk == null) {
                  chunk = this.getChunkAt(ChunkPos.getX(chunkKey), ChunkPos.getZ(chunkKey));
               }

               ((ImmutableList.Builder)ret.computeIfAbsent((Plugin)ticket.key, (key) -> {
                  return ImmutableList.builder();
               })).add(chunk);
            }
         }
      }

      return (Map)ret.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (entry) -> {
         return ((ImmutableList.Builder)entry.getValue()).build();
      }));
   }

   @NotNull
   public Collection<Chunk> getIntersectingChunks(@NotNull BoundingBox boundingBox) {
      List<Chunk> chunks = new ArrayList();
      int minX = NumberConversions.floor(boundingBox.getMinX()) >> 4;
      int maxX = NumberConversions.floor(boundingBox.getMaxX()) >> 4;
      int minZ = NumberConversions.floor(boundingBox.getMinZ()) >> 4;
      int maxZ = NumberConversions.floor(boundingBox.getMaxZ()) >> 4;

      for(int x = minX; x <= maxX; ++x) {
         for(int z = minZ; z <= maxZ; ++z) {
            chunks.add(this.getChunkAt(x, z, false));
         }
      }

      return chunks;
   }

   public boolean isChunkForceLoaded(int x, int z) {
      return this.getHandle().getForceLoadedChunks().contains(ChunkPos.asLong(x, z));
   }

   public void setChunkForceLoaded(int x, int z, boolean forced) {
      this.getHandle().setChunkForced(x, z, forced);
   }

   public Collection<Chunk> getForceLoadedChunks() {
      Set<Chunk> chunks = new HashSet();
      LongIterator var2 = this.getHandle().getForceLoadedChunks().iterator();

      while(var2.hasNext()) {
         long coord = (Long)var2.next();
         chunks.add(this.getChunkAt(ChunkPos.getX(coord), ChunkPos.getZ(coord)));
      }

      return Collections.unmodifiableCollection(chunks);
   }

   public ServerLevel getHandle() {
      return this.world;
   }

   public Item dropItem(Location loc, ItemStack item) {
      return this.dropItem(loc, item, (Consumer)null);
   }

   public Item dropItem(Location loc, ItemStack item, Consumer<? super Item> function) {
      Preconditions.checkArgument(loc != null, "Location cannot be null");
      Preconditions.checkArgument(item != null, "ItemStack cannot be null");
      ItemEntity entity = new ItemEntity(this.world, loc.getX(), loc.getY(), loc.getZ(), CraftItemStack.asNMSCopy(item));
      Item itemEntity = (Item)entity.getBukkitEntity();
      entity.pickupDelay = 10;
      if (function != null) {
         function.accept(itemEntity);
      }

      this.world.addFreshEntity(entity, SpawnReason.CUSTOM);
      return itemEntity;
   }

   public Item dropItemNaturally(Location loc, ItemStack item) {
      return this.dropItemNaturally(loc, item, (Consumer)null);
   }

   public Item dropItemNaturally(Location loc, ItemStack item, Consumer<? super Item> function) {
      Preconditions.checkArgument(loc != null, "Location cannot be null");
      Preconditions.checkArgument(item != null, "ItemStack cannot be null");
      double xs = Mth.nextDouble(this.world.random, -0.25, 0.25);
      double ys = Mth.nextDouble(this.world.random, -0.25, 0.25) - (double)EntityType.ITEM.getHeight() / 2.0;
      double zs = Mth.nextDouble(this.world.random, -0.25, 0.25);
      loc = loc.clone().add(xs, ys, zs);
      return this.dropItem(loc, item, function);
   }

   public Arrow spawnArrow(Location loc, Vector velocity, float speed, float spread) {
      return (Arrow)this.spawnArrow(loc, velocity, speed, spread, Arrow.class);
   }

   public <T extends AbstractArrow> T spawnArrow(Location loc, Vector velocity, float speed, float spread, Class<T> clazz) {
      Preconditions.checkArgument(loc != null, "Location cannot be null");
      Preconditions.checkArgument(velocity != null, "Vector cannot be null");
      Preconditions.checkArgument(clazz != null, "clazz Entity for the arrow cannot be null");
      net.minecraft.world.entity.projectile.AbstractArrow arrow;
      if (TippedArrow.class.isAssignableFrom(clazz)) {
         arrow = (net.minecraft.world.entity.projectile.AbstractArrow)EntityType.ARROW.create(this.world, EntitySpawnReason.COMMAND);
         ((Arrow)arrow.getBukkitEntity()).setBasePotionType(PotionType.WATER);
      } else if (SpectralArrow.class.isAssignableFrom(clazz)) {
         arrow = (net.minecraft.world.entity.projectile.AbstractArrow)EntityType.SPECTRAL_ARROW.create(this.world, EntitySpawnReason.COMMAND);
      } else if (Trident.class.isAssignableFrom(clazz)) {
         arrow = (net.minecraft.world.entity.projectile.AbstractArrow)EntityType.TRIDENT.create(this.world, EntitySpawnReason.COMMAND);
      } else {
         arrow = (net.minecraft.world.entity.projectile.AbstractArrow)EntityType.ARROW.create(this.world, EntitySpawnReason.COMMAND);
      }

      arrow.snapTo(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
      arrow.shoot(velocity.getX(), velocity.getY(), velocity.getZ(), speed, spread);
      this.world.addFreshEntity(arrow);
      return (AbstractArrow)arrow.getBukkitEntity();
   }

   public LightningStrike strikeLightning(Location loc) {
      return this.strikeLightning0(loc, false);
   }

   public LightningStrike strikeLightningEffect(Location loc) {
      return this.strikeLightning0(loc, true);
   }

   private LightningStrike strikeLightning0(Location loc, boolean isVisual) {
      Preconditions.checkArgument(loc != null, "Location cannot be null");
      LightningBolt lightning = (LightningBolt)EntityType.LIGHTNING_BOLT.create(this.world, EntitySpawnReason.COMMAND);
      lightning.snapTo(loc.getX(), loc.getY(), loc.getZ());
      lightning.setVisualOnly(isVisual);
      this.world.strikeLightning(lightning, Cause.CUSTOM);
      return (LightningStrike)lightning.getBukkitEntity();
   }

   public boolean generateTree(Location loc, TreeType type) {
      return this.generateTree(loc, rand, type);
   }

   public boolean generateTree(Location loc, TreeType type, BlockChangeDelegate delegate) {
      this.world.captureTreeGeneration = true;
      this.world.captureBlockStates = true;
      boolean grownTree = this.generateTree(loc, type);
      this.world.captureBlockStates = false;
      this.world.captureTreeGeneration = false;
      if (!grownTree) {
         this.world.capturedBlockStates.clear();
         return false;
      } else {
         Iterator var5 = this.world.capturedBlockStates.values().iterator();

         while(var5.hasNext()) {
            BlockState blockstate = (BlockState)var5.next();
            BlockPos position = ((CraftBlockState)blockstate).getPosition();
            net.minecraft.world.level.block.state.BlockState oldBlock = this.world.getBlockState(position);
            int flag = ((CraftBlockState)blockstate).getFlag();
            delegate.setBlockData(blockstate.getX(), blockstate.getY(), blockstate.getZ(), blockstate.getBlockData());
            net.minecraft.world.level.block.state.BlockState newBlock = this.world.getBlockState(position);
            this.world.notifyAndUpdatePhysics(position, (LevelChunk)null, oldBlock, newBlock, newBlock, flag, 512);
         }

         this.world.capturedBlockStates.clear();
         return true;
      }
   }

   public String getName() {
      return this.world.L.getLevelName();
   }

   public UUID getUID() {
      return this.world.uuid;
   }

   public NamespacedKey getKey() {
      return CraftNamespacedKey.fromMinecraft(this.world.dimension().location());
   }

   public String toString() {
      return "CraftWorld{name=" + this.getName() + "}";
   }

   public long getTime() {
      long time = this.getFullTime() % 24000L;
      if (time < 0L) {
         time += 24000L;
      }

      return time;
   }

   public void setTime(long time) {
      long margin = (time - this.getFullTime()) % 24000L;
      if (margin < 0L) {
         margin += 24000L;
      }

      this.setFullTime(this.getFullTime() + margin);
   }

   public long getFullTime() {
      return this.world.getDayTime();
   }

   public void setFullTime(long time) {
      TimeSkipEvent event = new TimeSkipEvent(this, SkipReason.CUSTOM, time - this.world.getDayTime());
      this.server.getPluginManager().callEvent(event);
      if (!event.isCancelled()) {
         this.world.setDayTime(this.world.getDayTime() + event.getSkipAmount());
         Iterator var4 = this.getPlayers().iterator();

         while(var4.hasNext()) {
            Player p = (Player)var4.next();
            CraftPlayer cp = (CraftPlayer)p;
            if (cp.getHandle().connection != null) {
               cp.getHandle().connection.send(new ClientboundSetTimePacket(cp.getHandle().level().getGameTime(), cp.getHandle().getPlayerTime(), cp.getHandle().level().getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
            }
         }

      }
   }

   public long getGameTime() {
      return this.world.levelData.getGameTime();
   }

   public boolean createExplosion(double x, double y, double z, float power) {
      return this.createExplosion(x, y, z, power, false, true);
   }

   public boolean createExplosion(double x, double y, double z, float power, boolean setFire) {
      return this.createExplosion(x, y, z, power, setFire, true);
   }

   public boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks) {
      return this.createExplosion(x, y, z, power, setFire, breakBlocks, (Entity)null);
   }

   public boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks, Entity source) {
      Level.ExplosionInteraction explosionType;
      if (!breakBlocks) {
         explosionType = ExplosionInteraction.NONE;
      } else if (source == null) {
         explosionType = ExplosionInteraction.STANDARD;
      } else {
         explosionType = ExplosionInteraction.MOB;
      }

      net.minecraft.world.entity.Entity entity = source == null ? null : ((CraftEntity)source).getHandle();
      return !this.world.explode0(entity, Explosion.getDefaultDamageSource(this.world, entity), (ExplosionDamageCalculator)null, x, y, z, power, setFire, explosionType, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE).wasCanceled;
   }

   public boolean createExplosion(Location loc, float power) {
      return this.createExplosion(loc, power, false);
   }

   public boolean createExplosion(Location loc, float power, boolean setFire) {
      return this.createExplosion(loc, power, setFire, true);
   }

   public boolean createExplosion(Location loc, float power, boolean setFire, boolean breakBlocks) {
      return this.createExplosion(loc, power, setFire, breakBlocks, (Entity)null);
   }

   public boolean createExplosion(Location loc, float power, boolean setFire, boolean breakBlocks, Entity source) {
      Preconditions.checkArgument(loc != null, "Location is null");
      Preconditions.checkArgument(this.equals(loc.getWorld()), "Location not in world");
      return this.createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, setFire, breakBlocks, source);
   }

   public World.Environment getEnvironment() {
      return this.environment;
   }

   public Block getBlockAt(Location location) {
      return this.getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
   }

   public Chunk getChunkAt(Location location) {
      return this.getChunkAt(location.getBlockX() >> 4, location.getBlockZ() >> 4);
   }

   public ChunkGenerator getGenerator() {
      return this.generator;
   }

   public BiomeProvider getBiomeProvider() {
      return this.biomeProvider;
   }

   public List<BlockPopulator> getPopulators() {
      return this.populators;
   }

   @NotNull
   public <T extends LivingEntity> T spawn(@NotNull Location location, @NotNull Class<T> clazz, @NotNull CreatureSpawnEvent.SpawnReason spawnReason, boolean randomizeData, @Nullable Consumer<? super T> function) throws IllegalArgumentException {
      Preconditions.checkArgument(spawnReason != null, "Spawn reason cannot be null");
      return (LivingEntity)this.spawn(location, clazz, function, spawnReason, randomizeData);
   }

   public Block getHighestBlockAt(int x, int z) {
      return this.getBlockAt(x, this.getHighestBlockYAt(x, z), z);
   }

   public Block getHighestBlockAt(Location location) {
      return this.getHighestBlockAt(location.getBlockX(), location.getBlockZ());
   }

   public int getHighestBlockYAt(int x, int z, HeightMap heightMap) {
      return this.world.getChunk(x >> 4, z >> 4).getHeight(CraftHeightMap.toNMS(heightMap), x, z);
   }

   public Block getHighestBlockAt(int x, int z, HeightMap heightMap) {
      return this.getBlockAt(x, this.getHighestBlockYAt(x, z, heightMap), z);
   }

   public Block getHighestBlockAt(Location location, HeightMap heightMap) {
      return this.getHighestBlockAt(location.getBlockX(), location.getBlockZ(), heightMap);
   }

   public Biome getBiome(int x, int z) {
      return this.getBiome(x, 0, z);
   }

   public void setBiome(int x, int z, Biome bio) {
      for(int y = this.getMinHeight(); y < this.getMaxHeight(); ++y) {
         this.setBiome(x, y, z, bio);
      }

   }

   public void setBiome(int x, int y, int z, Holder<net.minecraft.world.level.biome.Biome> bb) {
      BlockPos pos = new BlockPos(x, 0, z);
      if (this.world.hasChunkAt(pos)) {
         LevelChunk chunk = this.world.getChunkAt(pos);
         if (chunk != null) {
            chunk.setBiome(x >> 2, y >> 2, z >> 2, bb);
            chunk.markUnsaved();
         }
      }

   }

   public double getTemperature(int x, int z) {
      return this.getTemperature(x, 0, z);
   }

   public double getTemperature(int x, int y, int z) {
      BlockPos pos = new BlockPos(x, y, z);
      return (double)((net.minecraft.world.level.biome.Biome)this.world.getNoiseBiome(x >> 2, y >> 2, z >> 2).value()).getTemperature(pos, this.world.getSeaLevel());
   }

   public double getHumidity(int x, int z) {
      return this.getHumidity(x, 0, z);
   }

   public double getHumidity(int x, int y, int z) {
      return (double)((net.minecraft.world.level.biome.Biome)this.world.getNoiseBiome(x >> 2, y >> 2, z >> 2).value()).climateSettings.downfall();
   }

   /** @deprecated */
   @Deprecated
   public <T extends Entity> Collection<T> getEntitiesByClass(Class<T>... classes) {
      return this.getEntitiesByClasses(classes);
   }

   public Iterable<net.minecraft.world.entity.Entity> getNMSEntities() {
      return this.getHandle().getEntities().getAll();
   }

   public void addEntityToWorld(net.minecraft.world.entity.Entity entity, CreatureSpawnEvent.SpawnReason reason) {
      this.getHandle().addFreshEntity(entity, reason);
   }

   public void addEntityWithPassengers(net.minecraft.world.entity.Entity entity, CreatureSpawnEvent.SpawnReason reason) {
      this.getHandle().tryAddFreshEntityWithPassengers(entity, reason);
   }

   public Collection<Entity> getNearbyEntities(Location location, double x, double y, double z) {
      return this.getNearbyEntities(location, x, y, z, (Predicate)null);
   }

   public Collection<Entity> getNearbyEntities(Location location, double x, double y, double z, Predicate<? super Entity> filter) {
      Preconditions.checkArgument(location != null, "Location cannot be null");
      Preconditions.checkArgument(this.equals(location.getWorld()), "Location cannot be in a different world");
      BoundingBox aabb = BoundingBox.of(location, x, y, z);
      return this.getNearbyEntities(aabb, filter);
   }

   public Collection<Entity> getNearbyEntities(BoundingBox boundingBox) {
      return this.getNearbyEntities(boundingBox, (Predicate)null);
   }

   public Collection<Entity> getNearbyEntities(BoundingBox boundingBox, Predicate<? super Entity> filter) {
      AsyncCatcher.catchOp("getNearbyEntities");
      Preconditions.checkArgument(boundingBox != null, "BoundingBox cannot be null");
      AABB bb = new AABB(boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ(), boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ());
      List<net.minecraft.world.entity.Entity> entityList = this.getHandle().getEntities((net.minecraft.world.entity.Entity)null, bb, Predicates.alwaysTrue());
      List<Entity> bukkitEntityList = new ArrayList(entityList.size());
      Iterator var6 = entityList.iterator();

      while(true) {
         CraftEntity bukkitEntity;
         do {
            if (!var6.hasNext()) {
               return bukkitEntityList;
            }

            net.minecraft.world.entity.Entity entity = (net.minecraft.world.entity.Entity)var6.next();
            bukkitEntity = entity.getBukkitEntity();
         } while(filter != null && !filter.test(bukkitEntity));

         bukkitEntityList.add(bukkitEntity);
      }
   }

   public RayTraceResult rayTraceEntities(Location start, Vector direction, double maxDistance) {
      return this.rayTraceEntities(start, direction, maxDistance, (Predicate)null);
   }

   public RayTraceResult rayTraceEntities(Location start, Vector direction, double maxDistance, double raySize) {
      return this.rayTraceEntities(start, direction, maxDistance, raySize, (Predicate)null);
   }

   public RayTraceResult rayTraceEntities(Location start, Vector direction, double maxDistance, Predicate<? super Entity> filter) {
      return this.rayTraceEntities(start, direction, maxDistance, 0.0, filter);
   }

   public RayTraceResult rayTraceEntities(Location start, Vector direction, double maxDistance, double raySize, Predicate<? super Entity> filter) {
      Preconditions.checkArgument(start != null, "Location start cannot be null");
      Preconditions.checkArgument(this.equals(start.getWorld()), "Location start cannot be in a different world");
      start.checkFinite();
      Preconditions.checkArgument(direction != null, "Vector direction cannot be null");
      direction.checkFinite();
      Preconditions.checkArgument(direction.lengthSquared() > 0.0, "Direction's magnitude (%s) need to be greater than 0", direction.lengthSquared());
      if (maxDistance < 0.0) {
         return null;
      } else {
         Vector startPos = start.toVector();
         Vector dir = direction.clone().normalize().multiply(maxDistance);
         BoundingBox aabb = BoundingBox.of(startPos, startPos).expandDirectional(dir).expand(raySize);
         Collection<Entity> entities = this.getNearbyEntities(aabb, filter);
         Entity nearestHitEntity = null;
         RayTraceResult nearestHitResult = null;
         double nearestDistanceSq = Double.MAX_VALUE;
         Iterator var16 = entities.iterator();

         while(var16.hasNext()) {
            Entity entity = (Entity)var16.next();
            BoundingBox boundingBox = entity.getBoundingBox().expand(raySize);
            RayTraceResult hitResult = boundingBox.rayTrace(startPos, direction, maxDistance);
            if (hitResult != null) {
               double distanceSq = startPos.distanceSquared(hitResult.getHitPosition());
               if (distanceSq < nearestDistanceSq) {
                  nearestHitEntity = entity;
                  nearestHitResult = hitResult;
                  nearestDistanceSq = distanceSq;
               }
            }
         }

         return nearestHitEntity == null ? null : new RayTraceResult(nearestHitResult.getHitPosition(), nearestHitEntity, nearestHitResult.getHitBlockFace());
      }
   }

   public RayTraceResult rayTraceBlocks(Location start, Vector direction, double maxDistance) {
      return this.rayTraceBlocks(start, direction, maxDistance, FluidCollisionMode.NEVER, false);
   }

   public RayTraceResult rayTraceBlocks(Location start, Vector direction, double maxDistance, FluidCollisionMode fluidCollisionMode) {
      return this.rayTraceBlocks(start, direction, maxDistance, fluidCollisionMode, false);
   }

   public RayTraceResult rayTraceBlocks(Location start, Vector direction, double maxDistance, FluidCollisionMode fluidCollisionMode, boolean ignorePassableBlocks) {
      Preconditions.checkArgument(start != null, "Location start cannot be null");
      Preconditions.checkArgument(this.equals(start.getWorld()), "Location start cannot be in a different world");
      start.checkFinite();
      Preconditions.checkArgument(direction != null, "Vector direction cannot be null");
      direction.checkFinite();
      Preconditions.checkArgument(direction.lengthSquared() > 0.0, "Direction's magnitude (%s) need to be greater than 0", direction.lengthSquared());
      Preconditions.checkArgument(fluidCollisionMode != null, "FluidCollisionMode cannot be null");
      if (maxDistance < 0.0) {
         return null;
      } else {
         Vector dir = direction.clone().normalize().multiply(maxDistance);
         Vec3 startPos = CraftLocation.toVec3D(start);
         Vec3 endPos = startPos.add(dir.getX(), dir.getY(), dir.getZ());
         HitResult nmsHitResult = this.getHandle().clip(new ClipContext(startPos, endPos, ignorePassableBlocks ? net.minecraft.world.level.ClipContext.Block.COLLIDER : net.minecraft.world.level.ClipContext.Block.OUTLINE, CraftFluidCollisionMode.toNMS(fluidCollisionMode), CollisionContext.empty()));
         return CraftRayTraceResult.fromNMS(this, nmsHitResult);
      }
   }

   public RayTraceResult rayTrace(Location start, Vector direction, double maxDistance, FluidCollisionMode fluidCollisionMode, boolean ignorePassableBlocks, double raySize, Predicate<? super Entity> filter) {
      RayTraceResult blockHit = this.rayTraceBlocks(start, direction, maxDistance, fluidCollisionMode, ignorePassableBlocks);
      Vector startVec = null;
      double blockHitDistance = maxDistance;
      if (blockHit != null) {
         startVec = start.toVector();
         blockHitDistance = startVec.distance(blockHit.getHitPosition());
      }

      RayTraceResult entityHit = this.rayTraceEntities(start, direction, blockHitDistance, raySize, filter);
      if (blockHit == null) {
         return entityHit;
      } else if (entityHit == null) {
         return blockHit;
      } else {
         double entityHitDistanceSquared = startVec.distanceSquared(entityHit.getHitPosition());
         return entityHitDistanceSquared < blockHitDistance * blockHitDistance ? entityHit : blockHit;
      }
   }

   public List<Player> getPlayers() {
      List<Player> list = new ArrayList(this.world.players().size());
      Iterator var2 = this.world.players().iterator();

      while(var2.hasNext()) {
         net.minecraft.world.entity.player.Player human = (net.minecraft.world.entity.player.Player)var2.next();
         HumanEntity bukkitEntity = human.getBukkitEntity();
         if (bukkitEntity != null && bukkitEntity instanceof Player) {
            list.add((Player)bukkitEntity);
         }
      }

      return list;
   }

   public void save() {
      AsyncCatcher.catchOp("world save");
      this.server.checkSaveState();
      boolean oldSave = this.world.noSave;
      this.world.noSave = false;
      this.world.save((ProgressListener)null, false, false);
      this.world.noSave = oldSave;
   }

   public boolean isAutoSave() {
      return !this.world.noSave;
   }

   public void setAutoSave(boolean value) {
      this.world.noSave = !value;
   }

   public void setDifficulty(Difficulty difficulty) {
      this.getHandle().L.setDifficulty(net.minecraft.world.Difficulty.byId(difficulty.getValue()));
   }

   public Difficulty getDifficulty() {
      return Difficulty.getByValue(this.getHandle().getDifficulty().ordinal());
   }

   public int getViewDistance() {
      return this.world.getChunkSource().chunkMap.serverViewDistance;
   }

   public int getSimulationDistance() {
      return this.world.getChunkSource().chunkMap.getDistanceManager().simulationDistance;
   }

   public BlockMetadataStore getBlockMetadata() {
      return this.blockMetadata;
   }

   public boolean hasStorm() {
      return this.world.levelData.isRaining();
   }

   public void setStorm(boolean hasStorm) {
      this.world.levelData.setRaining(hasStorm);
      this.setWeatherDuration(0);
      this.setClearWeatherDuration(0);
   }

   public int getWeatherDuration() {
      return this.world.L.getRainTime();
   }

   public void setWeatherDuration(int duration) {
      this.world.L.setRainTime(duration);
   }

   public boolean isThundering() {
      return this.world.levelData.isThundering();
   }

   public void setThundering(boolean thundering) {
      this.world.L.setThundering(thundering);
      this.setThunderDuration(0);
      this.setClearWeatherDuration(0);
   }

   public int getThunderDuration() {
      return this.world.L.getThunderTime();
   }

   public void setThunderDuration(int duration) {
      this.world.L.setThunderTime(duration);
   }

   public boolean isClearWeather() {
      return !this.hasStorm() && !this.isThundering();
   }

   public void setClearWeatherDuration(int duration) {
      this.world.L.setClearWeatherTime(duration);
   }

   public int getClearWeatherDuration() {
      return this.world.L.getClearWeatherTime();
   }

   public long getSeed() {
      return this.world.getSeed();
   }

   public boolean getPVP() {
      return this.world.pvpMode;
   }

   public void setPVP(boolean pvp) {
      this.world.pvpMode = pvp;
   }

   public void playEffect(Player player, Effect effect, int data) {
      this.playEffect(player.getLocation(), effect, data, 0);
   }

   public void playEffect(Location location, Effect effect, int data) {
      this.playEffect(location, effect, data, 64);
   }

   public <T> void playEffect(Location loc, Effect effect, T data) {
      this.playEffect(loc, effect, data, 64);
   }

   public <T> void playEffect(Location loc, Effect effect, T data, int radius) {
      if (data != null) {
         Preconditions.checkArgument(effect.getData() != null, "Effect.%s does not have a valid Data", effect);
         Preconditions.checkArgument(effect.getData().isAssignableFrom(data.getClass()), "%s data cannot be used for the %s effect", data.getClass().getName(), effect);
      } else {
         Preconditions.checkArgument(effect.getData() == null || effect == Effect.ELECTRIC_SPARK, "Wrong kind of data for the %s effect", effect);
      }

      int datavalue = CraftEffect.getDataValue(effect, data);
      this.playEffect(loc, effect, datavalue, radius);
   }

   public void playEffect(Location location, Effect effect, int data, int radius) {
      Preconditions.checkArgument(effect != null, "Effect cannot be null");
      Preconditions.checkArgument(location != null, "Location cannot be null");
      Preconditions.checkArgument(location.getWorld() != null, "World of Location cannot be null");
      int packetData = effect.getId();
      ClientboundLevelEventPacket packet = new ClientboundLevelEventPacket(packetData, CraftLocation.toBlockPosition(location), data, false);
      radius *= radius;
      Iterator var8 = this.getPlayers().iterator();

      while(var8.hasNext()) {
         Player player = (Player)var8.next();
         if (((CraftPlayer)player).getHandle().connection != null && location.getWorld().equals(player.getWorld())) {
            int distance = (int)player.getLocation().distanceSquared(location);
            if (distance <= radius) {
               ((CraftPlayer)player).getHandle().connection.send(packet);
            }
         }
      }

   }

   public FallingBlock spawnFallingBlock(Location location, MaterialData data) throws IllegalArgumentException {
      Preconditions.checkArgument(data != null, "MaterialData cannot be null");
      return this.spawnFallingBlock(location, data.getItemType(), data.getData());
   }

   public FallingBlock spawnFallingBlock(Location location, Material material, byte data) throws IllegalArgumentException {
      Preconditions.checkArgument(location != null, "Location cannot be null");
      Preconditions.checkArgument(material != null, "Material cannot be null");
      Preconditions.checkArgument(material.isBlock(), "Material.%s must be a block", material);
      FallingBlockEntity entity = FallingBlockEntity.fall(this.world, BlockPos.containing(location.getX(), location.getY(), location.getZ()), CraftBlockType.bukkitToMinecraft(material).defaultBlockState(), SpawnReason.CUSTOM);
      return (FallingBlock)entity.getBukkitEntity();
   }

   public FallingBlock spawnFallingBlock(Location location, BlockData data) throws IllegalArgumentException {
      Preconditions.checkArgument(location != null, "Location cannot be null");
      Preconditions.checkArgument(data != null, "BlockData cannot be null");
      FallingBlockEntity entity = FallingBlockEntity.fall(this.world, BlockPos.containing(location.getX(), location.getY(), location.getZ()), ((CraftBlockData)data).getState(), SpawnReason.CUSTOM);
      return (FallingBlock)entity.getBukkitEntity();
   }

   public ChunkSnapshot getEmptyChunkSnapshot(int x, int z, boolean includeBiome, boolean includeBiomeTempRain) {
      return CraftChunk.getEmptyChunkSnapshot(x, z, this, includeBiome, includeBiomeTempRain);
   }

   public void setSpawnFlags(boolean allowMonsters, boolean allowAnimals) {
      this.world.getChunkSource().setSpawnSettings(allowMonsters, allowAnimals);
   }

   public boolean getAllowAnimals() {
      return this.world.getChunkSource().spawnFriendlies;
   }

   public boolean getAllowMonsters() {
      return this.world.getChunkSource().spawnEnemies;
   }

   public int getMinHeight() {
      return this.world.getMinY();
   }

   public int getMaxHeight() {
      return this.world.getMaxY() + 1;
   }

   public int getLogicalHeight() {
      return this.world.dimensionType().logicalHeight();
   }

   public boolean isNatural() {
      return this.world.dimensionType().natural();
   }

   public boolean isBedWorks() {
      return this.world.dimensionType().bedWorks();
   }

   public boolean hasSkyLight() {
      return this.world.dimensionType().hasSkyLight();
   }

   public boolean hasCeiling() {
      return this.world.dimensionType().hasCeiling();
   }

   public boolean isPiglinSafe() {
      return this.world.dimensionType().piglinSafe();
   }

   public boolean isRespawnAnchorWorks() {
      return this.world.dimensionType().respawnAnchorWorks();
   }

   public boolean hasRaids() {
      return this.world.dimensionType().hasRaids();
   }

   public boolean isUltraWarm() {
      return this.world.dimensionType().ultraWarm();
   }

   public int getSeaLevel() {
      return this.world.getSeaLevel();
   }

   public boolean getKeepSpawnInMemory() {
      return (Integer)this.getGameRuleValue(GameRule.SPAWN_CHUNK_RADIUS) > 0;
   }

   public void setKeepSpawnInMemory(boolean keepLoaded) {
      if (keepLoaded) {
         this.setGameRule(GameRule.SPAWN_CHUNK_RADIUS, (Integer)this.getGameRuleDefault(GameRule.SPAWN_CHUNK_RADIUS));
      } else {
         this.setGameRule(GameRule.SPAWN_CHUNK_RADIUS, 0);
      }

   }

   public int hashCode() {
      return this.getUID().hashCode();
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         CraftWorld other = (CraftWorld)obj;
         return this.getUID() == other.getUID();
      }
   }

   public File getWorldFolder() {
      return this.world.convertable.getLevelPath(LevelResource.ROOT).toFile().getParentFile();
   }

   public void sendPluginMessage(Plugin source, String channel, byte[] message) {
      StandardMessenger.validatePluginMessage(this.server.getMessenger(), source, channel, message);
      Iterator var4 = this.getPlayers().iterator();

      while(var4.hasNext()) {
         Player player = (Player)var4.next();
         player.sendPluginMessage(source, channel, message);
      }

   }

   public Set<String> getListeningPluginChannels() {
      Set<String> result = new HashSet();
      Iterator var2 = this.getPlayers().iterator();

      while(var2.hasNext()) {
         Player player = (Player)var2.next();
         result.addAll(player.getListeningPluginChannels());
      }

      return result;
   }

   public WorldType getWorldType() {
      return this.world.isFlat() ? WorldType.FLAT : WorldType.NORMAL;
   }

   public boolean canGenerateStructures() {
      return this.world.L.worldGenOptions().generateStructures();
   }

   public boolean isHardcore() {
      return this.world.getLevelData().isHardcore();
   }

   public void setHardcore(boolean hardcore) {
      this.world.L.settings.hardcore = hardcore;
   }

   /** @deprecated */
   @Deprecated
   public long getTicksPerAnimalSpawns() {
      return this.getTicksPerSpawns(SpawnCategory.ANIMAL);
   }

   /** @deprecated */
   @Deprecated
   public void setTicksPerAnimalSpawns(int ticksPerAnimalSpawns) {
      this.setTicksPerSpawns(SpawnCategory.ANIMAL, ticksPerAnimalSpawns);
   }

   /** @deprecated */
   @Deprecated
   public long getTicksPerMonsterSpawns() {
      return this.getTicksPerSpawns(SpawnCategory.MONSTER);
   }

   /** @deprecated */
   @Deprecated
   public void setTicksPerMonsterSpawns(int ticksPerMonsterSpawns) {
      this.setTicksPerSpawns(SpawnCategory.MONSTER, ticksPerMonsterSpawns);
   }

   /** @deprecated */
   @Deprecated
   public long getTicksPerWaterSpawns() {
      return this.getTicksPerSpawns(SpawnCategory.WATER_ANIMAL);
   }

   /** @deprecated */
   @Deprecated
   public void setTicksPerWaterSpawns(int ticksPerWaterSpawns) {
      this.setTicksPerSpawns(SpawnCategory.WATER_ANIMAL, ticksPerWaterSpawns);
   }

   /** @deprecated */
   @Deprecated
   public long getTicksPerWaterAmbientSpawns() {
      return this.getTicksPerSpawns(SpawnCategory.WATER_AMBIENT);
   }

   /** @deprecated */
   @Deprecated
   public void setTicksPerWaterAmbientSpawns(int ticksPerWaterAmbientSpawns) {
      this.setTicksPerSpawns(SpawnCategory.WATER_AMBIENT, ticksPerWaterAmbientSpawns);
   }

   /** @deprecated */
   @Deprecated
   public long getTicksPerWaterUndergroundCreatureSpawns() {
      return this.getTicksPerSpawns(SpawnCategory.WATER_UNDERGROUND_CREATURE);
   }

   /** @deprecated */
   @Deprecated
   public void setTicksPerWaterUndergroundCreatureSpawns(int ticksPerWaterUndergroundCreatureSpawns) {
      this.setTicksPerSpawns(SpawnCategory.WATER_UNDERGROUND_CREATURE, ticksPerWaterUndergroundCreatureSpawns);
   }

   /** @deprecated */
   @Deprecated
   public long getTicksPerAmbientSpawns() {
      return this.getTicksPerSpawns(SpawnCategory.AMBIENT);
   }

   /** @deprecated */
   @Deprecated
   public void setTicksPerAmbientSpawns(int ticksPerAmbientSpawns) {
      this.setTicksPerSpawns(SpawnCategory.AMBIENT, ticksPerAmbientSpawns);
   }

   public void setTicksPerSpawns(SpawnCategory spawnCategory, int ticksPerCategorySpawn) {
      Preconditions.checkArgument(spawnCategory != null, "SpawnCategory cannot be null");
      Preconditions.checkArgument(CraftSpawnCategory.isValidForLimits(spawnCategory), "SpawnCategory.%s are not supported", spawnCategory);
      this.world.ticksPerSpawnCategory.put(spawnCategory, (long)ticksPerCategorySpawn);
   }

   public long getTicksPerSpawns(SpawnCategory spawnCategory) {
      Preconditions.checkArgument(spawnCategory != null, "SpawnCategory cannot be null");
      Preconditions.checkArgument(CraftSpawnCategory.isValidForLimits(spawnCategory), "SpawnCategory.%s are not supported", spawnCategory);
      return this.world.ticksPerSpawnCategory.getLong(spawnCategory);
   }

   public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
      this.server.getWorldMetadata().setMetadata(this, metadataKey, newMetadataValue);
   }

   public List<MetadataValue> getMetadata(String metadataKey) {
      return this.server.getWorldMetadata().getMetadata(this, metadataKey);
   }

   public boolean hasMetadata(String metadataKey) {
      return this.server.getWorldMetadata().hasMetadata(this, metadataKey);
   }

   public void removeMetadata(String metadataKey, Plugin owningPlugin) {
      this.server.getWorldMetadata().removeMetadata(this, metadataKey, owningPlugin);
   }

   /** @deprecated */
   @Deprecated
   public int getMonsterSpawnLimit() {
      return this.getSpawnLimit(SpawnCategory.MONSTER);
   }

   /** @deprecated */
   @Deprecated
   public void setMonsterSpawnLimit(int limit) {
      this.setSpawnLimit(SpawnCategory.MONSTER, limit);
   }

   /** @deprecated */
   @Deprecated
   public int getAnimalSpawnLimit() {
      return this.getSpawnLimit(SpawnCategory.ANIMAL);
   }

   /** @deprecated */
   @Deprecated
   public void setAnimalSpawnLimit(int limit) {
      this.setSpawnLimit(SpawnCategory.ANIMAL, limit);
   }

   /** @deprecated */
   @Deprecated
   public int getWaterAnimalSpawnLimit() {
      return this.getSpawnLimit(SpawnCategory.WATER_ANIMAL);
   }

   /** @deprecated */
   @Deprecated
   public void setWaterAnimalSpawnLimit(int limit) {
      this.setSpawnLimit(SpawnCategory.WATER_ANIMAL, limit);
   }

   /** @deprecated */
   @Deprecated
   public int getWaterAmbientSpawnLimit() {
      return this.getSpawnLimit(SpawnCategory.WATER_AMBIENT);
   }

   /** @deprecated */
   @Deprecated
   public void setWaterAmbientSpawnLimit(int limit) {
      this.setSpawnLimit(SpawnCategory.WATER_AMBIENT, limit);
   }

   /** @deprecated */
   @Deprecated
   public int getWaterUndergroundCreatureSpawnLimit() {
      return this.getSpawnLimit(SpawnCategory.WATER_UNDERGROUND_CREATURE);
   }

   /** @deprecated */
   @Deprecated
   public void setWaterUndergroundCreatureSpawnLimit(int limit) {
      this.setSpawnLimit(SpawnCategory.WATER_UNDERGROUND_CREATURE, limit);
   }

   /** @deprecated */
   @Deprecated
   public int getAmbientSpawnLimit() {
      return this.getSpawnLimit(SpawnCategory.AMBIENT);
   }

   /** @deprecated */
   @Deprecated
   public void setAmbientSpawnLimit(int limit) {
      this.setSpawnLimit(SpawnCategory.AMBIENT, limit);
   }

   public int getSpawnLimit(SpawnCategory spawnCategory) {
      Preconditions.checkArgument(spawnCategory != null, "SpawnCategory cannot be null");
      Preconditions.checkArgument(CraftSpawnCategory.isValidForLimits(spawnCategory), "SpawnCategory.%s are not supported", spawnCategory);
      int limit = this.spawnCategoryLimit.getOrDefault(spawnCategory, -1);
      if (limit < 0) {
         limit = this.server.getSpawnLimit(spawnCategory);
      }

      return limit;
   }

   public void setSpawnLimit(SpawnCategory spawnCategory, int limit) {
      Preconditions.checkArgument(spawnCategory != null, "SpawnCategory cannot be null");
      Preconditions.checkArgument(CraftSpawnCategory.isValidForLimits(spawnCategory), "SpawnCategory.%s are not supported", spawnCategory);
      this.spawnCategoryLimit.put(spawnCategory, limit);
   }

   public void playNote(@NotNull Location loc, @NotNull Instrument instrument, @NotNull Note note) {
      this.playSound(loc, instrument.getSound(), SoundCategory.RECORDS, 3.0F, note.getPitch());
   }

   public void playSound(Location loc, Sound sound, float volume, float pitch) {
      this.playSound(loc, sound, SoundCategory.MASTER, volume, pitch);
   }

   public void playSound(Location loc, String sound, float volume, float pitch) {
      this.playSound(loc, sound, SoundCategory.MASTER, volume, pitch);
   }

   public void playSound(Location loc, Sound sound, SoundCategory category, float volume, float pitch) {
      this.playSound(loc, sound, category, volume, pitch, this.getHandle().random.nextLong());
   }

   public void playSound(Location loc, String sound, SoundCategory category, float volume, float pitch) {
      this.playSound(loc, sound, category, volume, pitch, this.getHandle().random.nextLong());
   }

   public void playSound(Location loc, Sound sound, SoundCategory category, float volume, float pitch, long seed) {
      if (loc != null && sound != null && category != null) {
         double x = loc.getX();
         double y = loc.getY();
         double z = loc.getZ();
         this.getHandle().playSeededSound((net.minecraft.world.entity.Entity)null, x, y, z, CraftSound.bukkitToMinecraft(sound), SoundSource.valueOf(category.name()), volume, pitch, seed);
      }
   }

   public void playSound(Location loc, String sound, SoundCategory category, float volume, float pitch, long seed) {
      if (loc != null && sound != null && category != null) {
         double x = loc.getX();
         double y = loc.getY();
         double z = loc.getZ();
         ClientboundSoundPacket packet = new ClientboundSoundPacket(Holder.direct(SoundEvent.createVariableRangeEvent(ResourceLocation.parse(sound))), SoundSource.valueOf(category.name()), x, y, z, volume, pitch, seed);
         this.world.getServer().getPlayerList().broadcast((net.minecraft.world.entity.player.Player)null, x, y, z, volume > 1.0F ? (double)(16.0F * volume) : 16.0, this.world.dimension(), packet);
      }
   }

   public void playSound(Entity entity, Sound sound, float volume, float pitch) {
      this.playSound(entity, sound, SoundCategory.MASTER, volume, pitch);
   }

   public void playSound(Entity entity, String sound, float volume, float pitch) {
      this.playSound(entity, sound, SoundCategory.MASTER, volume, pitch);
   }

   public void playSound(Entity entity, Sound sound, SoundCategory category, float volume, float pitch) {
      this.playSound(entity, sound, category, volume, pitch, this.getHandle().random.nextLong());
   }

   public void playSound(Entity entity, String sound, SoundCategory category, float volume, float pitch) {
      this.playSound(entity, sound, category, volume, pitch, this.getHandle().random.nextLong());
   }

   public void playSound(Entity entity, Sound sound, SoundCategory category, float volume, float pitch, long seed) {
      if (entity instanceof CraftEntity craftEntity) {
         if (entity.getWorld() == this && sound != null && category != null) {
            ClientboundSoundEntityPacket packet = new ClientboundSoundEntityPacket(CraftSound.bukkitToMinecraftHolder(sound), SoundSource.valueOf(category.name()), craftEntity.getHandle(), volume, pitch, seed);
            ChunkMap.TrackedEntity entityTracker = (ChunkMap.TrackedEntity)this.getHandle().getChunkSource().chunkMap.entityMap.get(entity.getEntityId());
            if (entityTracker != null) {
               entityTracker.broadcastAndSend(packet);
            }

            return;
         }
      }

   }

   public void playSound(Entity entity, String sound, SoundCategory category, float volume, float pitch, long seed) {
      if (entity instanceof CraftEntity craftEntity) {
         if (entity.getWorld() == this && sound != null && category != null) {
            ClientboundSoundEntityPacket packet = new ClientboundSoundEntityPacket(Holder.direct(SoundEvent.createVariableRangeEvent(ResourceLocation.parse(sound))), SoundSource.valueOf(category.name()), craftEntity.getHandle(), volume, pitch, seed);
            ChunkMap.TrackedEntity entityTracker = (ChunkMap.TrackedEntity)this.getHandle().getChunkSource().chunkMap.entityMap.get(entity.getEntityId());
            if (entityTracker != null) {
               entityTracker.broadcastAndSend(packet);
            }

            return;
         }
      }

   }

   public synchronized Map<String, GameRules.Key<?>> getGameRulesNMS() {
      return this.gamerules != null ? this.gamerules : (this.gamerules = getGameRulesNMS(this.getHandle().getGameRules()));
   }

   public static Map<String, GameRules.Key<?>> getGameRulesNMS(GameRules gameRules) {
      final Map<String, GameRules.Key<?>> gamerules = new HashMap();
      gameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
         public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> gamerules_gamerulekey, GameRules.Type<T> gamerules_gameruledefinition) {
            gamerules.put(gamerules_gamerulekey.getId(), gamerules_gamerulekey);
         }
      });
      return gamerules;
   }

   public synchronized Map<String, GameRules.Type<?>> getGameRuleDefinitions() {
      if (this.gameruleDefinitions != null) {
         return this.gameruleDefinitions;
      } else {
         final Map<String, GameRules.Type<?>> gameruleDefinitions = new HashMap();
         this.getHandle().getGameRules().visitGameRuleTypes(new GameRules.GameRuleTypeVisitor(this) {
            public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> gamerules_gamerulekey, GameRules.Type<T> gamerules_gameruledefinition) {
               gameruleDefinitions.put(gamerules_gamerulekey.getId(), gamerules_gameruledefinition);
            }
         });
         return this.gameruleDefinitions = gameruleDefinitions;
      }
   }

   public String getGameRuleValue(String rule) {
      if (rule == null) {
         return null;
      } else {
         GameRules.Value<?> value = this.getHandle().getGameRules().getRule((GameRules.Key)this.getGameRulesNMS().get(rule));
         return value != null ? value.toString() : "";
      }
   }

   public boolean setGameRuleValue(String rule, String value) {
      if (rule != null && value != null) {
         if (!this.isGameRule(rule)) {
            return false;
         } else {
            GameRules.Value<?> handle = this.getHandle().getGameRules().getRule((GameRules.Key)this.getGameRulesNMS().get(rule));
            handle.deserialize(value);
            handle.onChanged(this.getHandle());
            return true;
         }
      } else {
         return false;
      }
   }

   public String[] getGameRules() {
      return (String[])this.getGameRulesNMS().keySet().toArray(new String[this.getGameRulesNMS().size()]);
   }

   public boolean isGameRule(String rule) {
      Preconditions.checkArgument(rule != null, "String rule cannot be null");
      Preconditions.checkArgument(!rule.isEmpty(), "String rule cannot be empty");
      return this.getGameRulesNMS().containsKey(rule);
   }

   public <T> T getGameRuleValue(GameRule<T> rule) {
      Preconditions.checkArgument(rule != null, "GameRule cannot be null");
      return this.convert(rule, this.getHandle().getGameRules().getRule((GameRules.Key)this.getGameRulesNMS().get(rule.getName())));
   }

   public <T> T getGameRuleDefault(GameRule<T> rule) {
      Preconditions.checkArgument(rule != null, "GameRule cannot be null");
      return this.convert(rule, ((GameRules.Type)this.getGameRuleDefinitions().get(rule.getName())).createRule());
   }

   public <T> boolean setGameRule(GameRule<T> rule, T newValue) {
      Preconditions.checkArgument(rule != null, "GameRule cannot be null");
      Preconditions.checkArgument(newValue != null, "GameRule value cannot be null");
      if (!this.isGameRule(rule.getName())) {
         return false;
      } else {
         GameRules.Value<?> handle = this.getHandle().getGameRules().getRule((GameRules.Key)this.getGameRulesNMS().get(rule.getName()));
         handle.deserialize(newValue.toString());
         handle.onChanged(this.getHandle());
         return true;
      }
   }

   private <T> T convert(GameRule<T> rule, GameRules.Value<?> value) {
      if (value == null) {
         return null;
      } else if (value instanceof GameRules.BooleanValue) {
         return rule.getType().cast(((GameRules.BooleanValue)value).get());
      } else if (value instanceof GameRules.IntegerValue) {
         return rule.getType().cast(value.getCommandResult());
      } else {
         String var10002 = String.valueOf(value);
         throw new IllegalArgumentException("Invalid GameRule type (" + var10002 + ") for GameRule " + rule.getName());
      }
   }

   public WorldBorder getWorldBorder() {
      if (this.worldBorder == null) {
         this.worldBorder = new CraftWorldBorder(this);
      }

      return this.worldBorder;
   }

   public void spawnParticle(Particle particle, Location location, int count) {
      this.spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count);
   }

   public void spawnParticle(Particle particle, double x, double y, double z, int count) {
      this.spawnParticle(particle, x, y, z, count, (Object)null);
   }

   public <T> void spawnParticle(Particle particle, Location location, int count, T data) {
      this.spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, data);
   }

   public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, T data) {
      this.spawnParticle(particle, x, y, z, count, 0.0, 0.0, 0.0, data);
   }

   public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ) {
      this.spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ);
   }

   public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ) {
      this.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, (Object)null);
   }

   public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, T data) {
      this.spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, data);
   }

   public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, T data) {
      this.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, 1.0, data);
   }

   public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra) {
      this.spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra);
   }

   public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra) {
      this.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, (Object)null);
   }

   public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
      this.spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra, data);
   }

   public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
      this.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, data, false);
   }

   public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, T data, boolean force) {
      this.spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra, data, force);
   }

   public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, T data, boolean force) {
      this.getHandle().sendParticlesSource((ServerPlayer)null, CraftParticle.createParticleParam(particle, data), force, false, x, y, z, count, offsetX, offsetY, offsetZ, extra);
   }

   /** @deprecated */
   @Deprecated
   public Location locateNearestStructure(Location origin, StructureType structureType, int radius, boolean findUnexplored) {
      StructureSearchResult result = null;
      if (StructureType.MINESHAFT == structureType) {
         result = this.locateNearestStructure(origin, org.bukkit.generator.structure.StructureType.MINESHAFT, radius, findUnexplored);
      } else if (StructureType.VILLAGE == structureType) {
         result = this.locateNearestStructure(origin, List.of(Structure.VILLAGE_DESERT, Structure.VILLAGE_PLAINS, Structure.VILLAGE_SAVANNA, Structure.VILLAGE_SNOWY, Structure.VILLAGE_TAIGA), radius, findUnexplored);
      } else if (StructureType.NETHER_FORTRESS == structureType) {
         result = this.locateNearestStructure(origin, org.bukkit.generator.structure.StructureType.FORTRESS, radius, findUnexplored);
      } else if (StructureType.STRONGHOLD == structureType) {
         result = this.locateNearestStructure(origin, org.bukkit.generator.structure.StructureType.STRONGHOLD, radius, findUnexplored);
      } else if (StructureType.JUNGLE_PYRAMID == structureType) {
         result = this.locateNearestStructure(origin, org.bukkit.generator.structure.StructureType.JUNGLE_TEMPLE, radius, findUnexplored);
      } else if (StructureType.OCEAN_RUIN == structureType) {
         result = this.locateNearestStructure(origin, org.bukkit.generator.structure.StructureType.OCEAN_RUIN, radius, findUnexplored);
      } else if (StructureType.DESERT_PYRAMID == structureType) {
         result = this.locateNearestStructure(origin, org.bukkit.generator.structure.StructureType.DESERT_PYRAMID, radius, findUnexplored);
      } else if (StructureType.IGLOO == structureType) {
         result = this.locateNearestStructure(origin, org.bukkit.generator.structure.StructureType.IGLOO, radius, findUnexplored);
      } else if (StructureType.SWAMP_HUT == structureType) {
         result = this.locateNearestStructure(origin, org.bukkit.generator.structure.StructureType.SWAMP_HUT, radius, findUnexplored);
      } else if (StructureType.OCEAN_MONUMENT == structureType) {
         result = this.locateNearestStructure(origin, org.bukkit.generator.structure.StructureType.OCEAN_MONUMENT, radius, findUnexplored);
      } else if (StructureType.END_CITY == structureType) {
         result = this.locateNearestStructure(origin, org.bukkit.generator.structure.StructureType.END_CITY, radius, findUnexplored);
      } else if (StructureType.WOODLAND_MANSION == structureType) {
         result = this.locateNearestStructure(origin, org.bukkit.generator.structure.StructureType.WOODLAND_MANSION, radius, findUnexplored);
      } else if (StructureType.BURIED_TREASURE == structureType) {
         result = this.locateNearestStructure(origin, org.bukkit.generator.structure.StructureType.BURIED_TREASURE, radius, findUnexplored);
      } else if (StructureType.SHIPWRECK == structureType) {
         result = this.locateNearestStructure(origin, org.bukkit.generator.structure.StructureType.SHIPWRECK, radius, findUnexplored);
      } else if (StructureType.PILLAGER_OUTPOST == structureType) {
         result = this.locateNearestStructure(origin, Structure.PILLAGER_OUTPOST, radius, findUnexplored);
      } else if (StructureType.NETHER_FOSSIL == structureType) {
         result = this.locateNearestStructure(origin, org.bukkit.generator.structure.StructureType.NETHER_FOSSIL, radius, findUnexplored);
      } else if (StructureType.RUINED_PORTAL == structureType) {
         result = this.locateNearestStructure(origin, org.bukkit.generator.structure.StructureType.RUINED_PORTAL, radius, findUnexplored);
      } else if (StructureType.BASTION_REMNANT == structureType) {
         result = this.locateNearestStructure(origin, Structure.BASTION_REMNANT, radius, findUnexplored);
      }

      return result == null ? null : result.getLocation();
   }

   public StructureSearchResult locateNearestStructure(Location origin, org.bukkit.generator.structure.StructureType structureType, int radius, boolean findUnexplored) {
      List<Structure> structures = new ArrayList();
      Iterator var6 = Registry.STRUCTURE.iterator();

      while(var6.hasNext()) {
         Structure structure = (Structure)var6.next();
         if (structure.getStructureType() == structureType) {
            structures.add(structure);
         }
      }

      return this.locateNearestStructure(origin, (List)structures, radius, findUnexplored);
   }

   public StructureSearchResult locateNearestStructure(Location origin, Structure structure, int radius, boolean findUnexplored) {
      return this.locateNearestStructure(origin, List.of(structure), radius, findUnexplored);
   }

   public StructureSearchResult locateNearestStructure(Location origin, List<Structure> structures, int radius, boolean findUnexplored) {
      BlockPos originPos = BlockPos.containing(origin.getX(), origin.getY(), origin.getZ());
      List<Holder<net.minecraft.world.level.levelgen.structure.Structure>> holders = new ArrayList();
      Iterator var7 = structures.iterator();

      while(var7.hasNext()) {
         Structure structure = (Structure)var7.next();
         holders.add(Holder.direct(CraftStructure.bukkitToMinecraft(structure)));
      }

      Pair<BlockPos, Holder<net.minecraft.world.level.levelgen.structure.Structure>> found = this.getHandle().getChunkSource().getGenerator().findNearestMapStructure(this.getHandle(), HolderSet.direct(holders), originPos, radius, findUnexplored);
      return found == null ? null : new CraftStructureSearchResult(CraftStructure.minecraftToBukkit((net.minecraft.world.level.levelgen.structure.Structure)((Holder)found.getSecond()).value()), CraftLocation.toBukkit((BlockPos)((BlockPos)found.getFirst()), (World)this));
   }

   public BiomeSearchResult locateNearestBiome(Location origin, int radius, Biome... biomes) {
      return this.locateNearestBiome(origin, radius, 32, 64, biomes);
   }

   public BiomeSearchResult locateNearestBiome(Location origin, int radius, int horizontalInterval, int verticalInterval, Biome... biomes) {
      BlockPos originPos = BlockPos.containing(origin.getX(), origin.getY(), origin.getZ());
      Set<Holder<net.minecraft.world.level.biome.Biome>> holders = new HashSet();
      Biome[] var8 = biomes;
      int var9 = biomes.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         Biome biome = var8[var10];
         holders.add(CraftBiome.bukkitToMinecraftHolder(biome));
      }

      Climate.Sampler sampler = this.getHandle().getChunkSource().randomState().sampler();
      BiomeSource var10000 = this.getHandle().getChunkSource().getGenerator().getBiomeSource();
      Objects.requireNonNull(holders);
      Pair<BlockPos, Holder<net.minecraft.world.level.biome.Biome>> found = var10000.findClosestBiome3d(originPos, radius, horizontalInterval, verticalInterval, holders::contains, sampler, this.getHandle());
      return found == null ? null : new CraftBiomeSearchResult(CraftBiome.minecraftHolderToBukkit((Holder)found.getSecond()), new Location(this, (double)((BlockPos)found.getFirst()).getX(), (double)((BlockPos)found.getFirst()).getY(), (double)((BlockPos)found.getFirst()).getZ()));
   }

   public Raid locateNearestRaid(Location location, int radius) {
      Preconditions.checkArgument(location != null, "Location cannot be null");
      Preconditions.checkArgument(radius >= 0, "Radius value (%s) cannot be negative", radius);
      Raids persistentRaid = this.world.getRaids();
      net.minecraft.world.entity.raid.Raid raid = persistentRaid.getNearbyRaid(CraftLocation.toBlockPosition(location), radius * radius);
      return raid == null ? null : new CraftRaid(raid, this.world);
   }

   public List<Raid> getRaids() {
      Raids persistentRaid = this.world.getRaids();
      return (List)persistentRaid.raidMap.values().stream().map((raid) -> {
         return new CraftRaid(raid, this.world);
      }).collect(Collectors.toList());
   }

   public DragonBattle getEnderDragonBattle() {
      return this.getHandle().getDragonFight() == null ? null : new CraftDragonBattle(this.getHandle().getDragonFight());
   }

   public Collection<GeneratedStructure> getStructures(int x, int z) {
      return this.getStructures(x, z, (struct) -> {
         return true;
      });
   }

   public Collection<GeneratedStructure> getStructures(int x, int z, Structure structure) {
      Preconditions.checkArgument(structure != null, "Structure cannot be null");
      net.minecraft.core.Registry<net.minecraft.world.level.levelgen.structure.Structure> registry = CraftRegistry.getMinecraftRegistry(Registries.STRUCTURE);
      ResourceLocation key = registry.getKey(CraftStructure.bukkitToMinecraft(structure));
      return this.getStructures(x, z, (struct) -> {
         return registry.getKey(struct).equals(key);
      });
   }

   private List<GeneratedStructure> getStructures(int x, int z, Predicate<net.minecraft.world.level.levelgen.structure.Structure> predicate) {
      List<GeneratedStructure> structures = new ArrayList();
      Iterator var5 = this.getHandle().structureManager().startsForStructure(new ChunkPos(x, z), predicate).iterator();

      while(var5.hasNext()) {
         StructureStart start = (StructureStart)var5.next();
         structures.add(new CraftGeneratedStructure(start));
      }

      return structures;
   }

   public PersistentDataContainer getPersistentDataContainer() {
      return this.persistentDataContainer;
   }

   public Set<FeatureFlag> getFeatureFlags() {
      Stream var10000 = CraftFeatureFlag.getFromNMS(this.getHandle().enabledFeatures()).stream();
      Objects.requireNonNull(FeatureFlag.class);
      return (Set)var10000.map(FeatureFlag.class::cast).collect(Collectors.toUnmodifiableSet());
   }

   public void storeBukkitValues(CompoundTag c) {
      if (!this.persistentDataContainer.isEmpty()) {
         c.put("BukkitValues", this.persistentDataContainer.toTagCompound());
      }

   }

   public void readBukkitValues(Tag c) {
      if (c instanceof CompoundTag) {
         this.persistentDataContainer.putAll((CompoundTag)c);
      }

   }

   public World.Spigot spigot() {
      return this.spigot;
   }
}
