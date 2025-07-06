package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.portal.TeleportTransition;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.RegionAccessor;
import org.bukkit.TreeType;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.CraftBiome;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftEntityTypes;
import org.bukkit.craftbukkit.util.BlockStateListPopulator;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.RandomSourceWrapper;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.AbstractCow;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.SizedFireball;
import org.bukkit.entity.TippedArrow;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.potion.PotionType;

public abstract class CraftRegionAccessor implements RegionAccessor {
   public abstract WorldGenLevel getHandle();

   public boolean isNormalWorld() {
      return this.getHandle() instanceof ServerLevel;
   }

   public Biome getBiome(Location location) {
      return this.getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ());
   }

   public Biome getBiome(int x, int y, int z) {
      return CraftBiome.minecraftHolderToBukkit(this.getHandle().getNoiseBiome(x >> 2, y >> 2, z >> 2));
   }

   public void setBiome(Location location, Biome biome) {
      this.setBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ(), biome);
   }

   public void setBiome(int x, int y, int z, Biome biome) {
      Preconditions.checkArgument(biome != Biome.CUSTOM, "Cannot set the biome to %s", biome);
      Holder<net.minecraft.world.level.biome.Biome> biomeBase = CraftBiome.bukkitToMinecraftHolder(biome);
      this.setBiome(x, y, z, biomeBase);
   }

   public abstract void setBiome(int var1, int var2, int var3, Holder<net.minecraft.world.level.biome.Biome> var4);

   public BlockState getBlockState(Location location) {
      return this.getBlockState(location.getBlockX(), location.getBlockY(), location.getBlockZ());
   }

   public BlockState getBlockState(int x, int y, int z) {
      return CraftBlock.at(this.getHandle(), new BlockPos(x, y, z)).getState();
   }

   public BlockData getBlockData(Location location) {
      return this.getBlockData(location.getBlockX(), location.getBlockY(), location.getBlockZ());
   }

   public BlockData getBlockData(int x, int y, int z) {
      return CraftBlockData.fromData(this.getData(x, y, z));
   }

   public Material getType(Location location) {
      return this.getType(location.getBlockX(), location.getBlockY(), location.getBlockZ());
   }

   public Material getType(int x, int y, int z) {
      return CraftBlockType.minecraftToBukkit(this.getData(x, y, z).getBlock());
   }

   private net.minecraft.world.level.block.state.BlockState getData(int x, int y, int z) {
      return this.getHandle().getBlockState(new BlockPos(x, y, z));
   }

   public void setBlockData(Location location, BlockData blockData) {
      this.setBlockData(location.getBlockX(), location.getBlockY(), location.getBlockZ(), blockData);
   }

   public void setBlockData(int x, int y, int z, BlockData blockData) {
      WorldGenLevel world = this.getHandle();
      BlockPos pos = new BlockPos(x, y, z);
      net.minecraft.world.level.block.state.BlockState old = this.getHandle().getBlockState(pos);
      CraftBlock.setTypeAndData(world, pos, old, ((CraftBlockData)blockData).getState(), true);
   }

   public void setType(Location location, Material material) {
      this.setType(location.getBlockX(), location.getBlockY(), location.getBlockZ(), material);
   }

   public void setType(int x, int y, int z, Material material) {
      this.setBlockData(x, y, z, material.createBlockData());
   }

   public int getHighestBlockYAt(int x, int z) {
      return this.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING);
   }

   public int getHighestBlockYAt(Location location) {
      return this.getHighestBlockYAt(location.getBlockX(), location.getBlockZ());
   }

   public int getHighestBlockYAt(int x, int z, HeightMap heightMap) {
      return this.getHandle().getHeight(CraftHeightMap.toNMS(heightMap), x, z);
   }

   public int getHighestBlockYAt(Location location, HeightMap heightMap) {
      return this.getHighestBlockYAt(location.getBlockX(), location.getBlockZ(), heightMap);
   }

   public boolean generateTree(Location location, Random random, TreeType treeType) {
      BlockPos pos = CraftLocation.toBlockPosition(location);
      return this.generateTree(this.getHandle(), this.getHandle().getMinecraftWorld().getChunkSource().getGenerator(), pos, new RandomSourceWrapper(random), treeType);
   }

   public boolean generateTree(Location location, Random random, TreeType treeType, Consumer<? super BlockState> consumer) {
      return this.generateTree(location, random, treeType, consumer == null ? null : (block) -> {
         consumer.accept(block);
         return true;
      });
   }

   public boolean generateTree(Location location, Random random, TreeType treeType, Predicate<? super BlockState> predicate) {
      BlockPos pos = CraftLocation.toBlockPosition(location);
      BlockStateListPopulator populator = new BlockStateListPopulator(this.getHandle());
      boolean result = this.generateTree(populator, this.getHandle().getMinecraftWorld().getChunkSource().getGenerator(), pos, new RandomSourceWrapper(random), treeType);
      populator.refreshTiles();
      Iterator var8 = populator.getList().iterator();

      while(true) {
         BlockState blockState;
         do {
            if (!var8.hasNext()) {
               return result;
            }

            blockState = (BlockState)var8.next();
         } while(predicate != null && !predicate.test(blockState));

         blockState.update(true, true);
      }
   }

   public boolean generateTree(WorldGenLevel access, ChunkGenerator chunkGenerator, BlockPos pos, RandomSource random, TreeType treeType) {
      ResourceKey gen;
      switch (treeType) {
         case BIG_TREE:
            gen = TreeFeatures.FANCY_OAK;
            break;
         case BIRCH:
            gen = TreeFeatures.BIRCH;
            break;
         case REDWOOD:
            gen = TreeFeatures.SPRUCE;
            break;
         case TALL_REDWOOD:
            gen = TreeFeatures.PINE;
            break;
         case JUNGLE:
            gen = TreeFeatures.MEGA_JUNGLE_TREE;
            break;
         case SMALL_JUNGLE:
            gen = TreeFeatures.JUNGLE_TREE_NO_VINE;
            break;
         case COCOA_TREE:
            gen = TreeFeatures.JUNGLE_TREE;
            break;
         case JUNGLE_BUSH:
            gen = TreeFeatures.JUNGLE_BUSH;
            break;
         case RED_MUSHROOM:
            gen = TreeFeatures.HUGE_RED_MUSHROOM;
            break;
         case BROWN_MUSHROOM:
            gen = TreeFeatures.HUGE_BROWN_MUSHROOM;
            break;
         case SWAMP:
            gen = TreeFeatures.SWAMP_OAK;
            break;
         case ACACIA:
            gen = TreeFeatures.ACACIA;
            break;
         case DARK_OAK:
            gen = TreeFeatures.DARK_OAK;
            break;
         case MEGA_REDWOOD:
            gen = TreeFeatures.MEGA_SPRUCE;
            break;
         case MEGA_PINE:
            gen = TreeFeatures.MEGA_PINE;
            break;
         case TALL_BIRCH:
            gen = TreeFeatures.SUPER_BIRCH_BEES_0002;
            break;
         case CHORUS_PLANT:
            ChorusFlowerBlock var10000 = (ChorusFlowerBlock)Blocks.CHORUS_FLOWER;
            ChorusFlowerBlock.generatePlant(access, pos, random, 8);
            return true;
         case CRIMSON_FUNGUS:
            gen = TreeFeatures.CRIMSON_FUNGUS_PLANTED;
            break;
         case WARPED_FUNGUS:
            gen = TreeFeatures.WARPED_FUNGUS_PLANTED;
            break;
         case AZALEA:
            gen = TreeFeatures.AZALEA_TREE;
            break;
         case MANGROVE:
            gen = TreeFeatures.MANGROVE;
            break;
         case TALL_MANGROVE:
            gen = TreeFeatures.TALL_MANGROVE;
            break;
         case CHERRY:
            gen = TreeFeatures.CHERRY;
            break;
         case PALE_OAK:
            gen = TreeFeatures.PALE_OAK;
            break;
         case PALE_OAK_CREAKING:
            gen = TreeFeatures.PALE_OAK_CREAKING;
            break;
         case TREE:
         default:
            gen = TreeFeatures.OAK;
      }

      Holder<ConfiguredFeature<?, ?>> holder = (Holder)access.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).get(gen).orElse((Object)null);
      return holder != null ? ((ConfiguredFeature)holder.value()).place(access, chunkGenerator, random, pos) : false;
   }

   public Entity spawnEntity(Location location, EntityType entityType) {
      return this.spawn(location, entityType.getEntityClass());
   }

   public Entity spawnEntity(Location loc, EntityType type, boolean randomizeData) {
      return this.spawn(loc, type.getEntityClass(), (Consumer)null, SpawnReason.CUSTOM, randomizeData);
   }

   public List<Entity> getEntities() {
      List<Entity> list = new ArrayList();
      this.getNMSEntities().forEach((entity) -> {
         Entity bukkitEntity = entity.getBukkitEntity();
         if (bukkitEntity != null && (!this.isNormalWorld() || bukkitEntity.isValid())) {
            list.add(bukkitEntity);
         }

      });
      return list;
   }

   public List<LivingEntity> getLivingEntities() {
      List<LivingEntity> list = new ArrayList();
      this.getNMSEntities().forEach((entity) -> {
         Entity bukkitEntity = entity.getBukkitEntity();
         if (bukkitEntity != null && bukkitEntity instanceof LivingEntity && (!this.isNormalWorld() || bukkitEntity.isValid())) {
            list.add((LivingEntity)bukkitEntity);
         }

      });
      return list;
   }

   public <T extends Entity> Collection<T> getEntitiesByClass(Class<T> clazz) {
      Collection<T> list = new ArrayList();
      this.getNMSEntities().forEach((entity) -> {
         Entity bukkitEntity = entity.getBukkitEntity();
         if (bukkitEntity != null) {
            Class<?> bukkitClass = bukkitEntity.getClass();
            if (clazz.isAssignableFrom(bukkitClass) && (!this.isNormalWorld() || bukkitEntity.isValid())) {
               list.add(bukkitEntity);
            }

         }
      });
      return list;
   }

   public Collection<Entity> getEntitiesByClasses(Class<?>... classes) {
      Collection<Entity> list = new ArrayList();
      this.getNMSEntities().forEach((entity) -> {
         Entity bukkitEntity = entity.getBukkitEntity();
         if (bukkitEntity != null) {
            Class<?> bukkitClass = bukkitEntity.getClass();
            Class[] var6 = classes;
            int var7 = classes.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               Class<?> clazz = var6[var8];
               if (clazz.isAssignableFrom(bukkitClass)) {
                  if (!this.isNormalWorld() || bukkitEntity.isValid()) {
                     list.add(bukkitEntity);
                  }
                  break;
               }
            }

         }
      });
      return list;
   }

   public abstract Iterable<net.minecraft.world.entity.Entity> getNMSEntities();

   public <T extends Entity> T createEntity(Location location, Class<T> clazz) throws IllegalArgumentException {
      net.minecraft.world.entity.Entity entity = this.createEntity(location, clazz, true);
      if (!this.isNormalWorld()) {
         entity.generation = true;
      }

      return entity.getBukkitEntity();
   }

   public <T extends Entity> T spawn(Location location, Class<T> clazz) throws IllegalArgumentException {
      return this.spawn(location, clazz, (Consumer)null, SpawnReason.CUSTOM);
   }

   public <T extends Entity> T spawn(Location location, Class<T> clazz, Consumer<? super T> function) throws IllegalArgumentException {
      return this.spawn(location, clazz, function, SpawnReason.CUSTOM);
   }

   public <T extends Entity> T spawn(Location location, Class<T> clazz, boolean randomizeData, Consumer<? super T> function) throws IllegalArgumentException {
      return this.spawn(location, clazz, function, SpawnReason.CUSTOM, randomizeData);
   }

   public <T extends Entity> T spawn(Location location, Class<T> clazz, Consumer<? super T> function, CreatureSpawnEvent.SpawnReason reason) throws IllegalArgumentException {
      return this.spawn(location, clazz, function, reason, true);
   }

   public <T extends Entity> T spawn(Location location, Class<T> clazz, Consumer<? super T> function, CreatureSpawnEvent.SpawnReason reason, boolean randomizeData) throws IllegalArgumentException {
      net.minecraft.world.entity.Entity entity = this.createEntity(location, clazz, randomizeData);
      return this.addEntity(entity, reason, function, randomizeData);
   }

   public <T extends Entity> T addEntity(T entity) {
      Preconditions.checkArgument(!entity.isInWorld(), "Entity has already been added to a world");
      net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity)entity).getHandle();
      if (nmsEntity.level() != this.getHandle().getLevel()) {
         nmsEntity = nmsEntity.teleport(new TeleportTransition(this.getHandle().getLevel(), nmsEntity, TeleportTransition.DO_NOTHING));
      }

      this.addEntityWithPassengers(nmsEntity, SpawnReason.CUSTOM);
      return nmsEntity.getBukkitEntity();
   }

   public <T extends Entity> T addEntity(net.minecraft.world.entity.Entity entity, CreatureSpawnEvent.SpawnReason reason) throws IllegalArgumentException {
      return this.addEntity(entity, reason, (Consumer)null, true);
   }

   public <T extends Entity> T addEntity(net.minecraft.world.entity.Entity entity, CreatureSpawnEvent.SpawnReason reason, Consumer<? super T> function, boolean randomizeData) throws IllegalArgumentException {
      Preconditions.checkArgument(entity != null, "Cannot spawn null entity");
      if (randomizeData && entity instanceof Mob) {
         ((Mob)entity).finalizeSpawn(this.getHandle(), this.getHandle().getCurrentDifficultyAt(entity.blockPosition()), EntitySpawnReason.COMMAND, (SpawnGroupData)null);
      }

      if (!this.isNormalWorld()) {
         entity.generation = true;
      }

      if (function != null) {
         function.accept(entity.getBukkitEntity());
      }

      this.addEntityToWorld(entity, reason);
      return entity.getBukkitEntity();
   }

   public abstract void addEntityToWorld(net.minecraft.world.entity.Entity var1, CreatureSpawnEvent.SpawnReason var2);

   public abstract void addEntityWithPassengers(net.minecraft.world.entity.Entity var1, CreatureSpawnEvent.SpawnReason var2);

   public net.minecraft.world.entity.Entity makeEntity(Location location, Class<? extends Entity> clazz) throws IllegalArgumentException {
      return this.createEntity(location, clazz, true);
   }

   public net.minecraft.world.entity.Entity createEntity(Location location, Class<? extends Entity> clazz, boolean randomizeData) throws IllegalArgumentException {
      Preconditions.checkArgument(location != null, "Location cannot be null");
      Preconditions.checkArgument(clazz != null, "Entity class cannot be null");
      Consumer<net.minecraft.world.entity.Entity> runOld = (other) -> {
      };
      if (clazz == AbstractArrow.class) {
         clazz = Arrow.class;
      } else if (clazz == AbstractHorse.class) {
         clazz = Horse.class;
      } else if (clazz == AbstractCow.class) {
         clazz = Cow.class;
      } else if (clazz == Fireball.class) {
         clazz = LargeFireball.class;
      } else if (clazz == Minecart.class) {
         clazz = RideableMinecart.class;
      } else if (clazz == SizedFireball.class) {
         clazz = LargeFireball.class;
      } else if (clazz == TippedArrow.class) {
         clazz = Arrow.class;
         runOld = (other) -> {
            ((Arrow)other.getBukkitEntity()).setBasePotionType(PotionType.WATER);
         };
      }

      CraftEntityTypes.EntityTypeData<?, ?> entityTypeData = CraftEntityTypes.getEntityTypeData(clazz);
      if (entityTypeData != null && entityTypeData.spawnFunction() != null) {
         if (!entityTypeData.entityType().isEnabledByFeature(this.getHandle().getMinecraftWorld().getWorld())) {
            throw new IllegalArgumentException("Cannot spawn an entity for " + clazz.getName() + " because it is not an enabled feature");
         } else {
            net.minecraft.world.entity.Entity entity = (net.minecraft.world.entity.Entity)entityTypeData.spawnFunction().apply(new CraftEntityTypes.SpawnData(this.getHandle(), location, randomizeData, this.isNormalWorld()));
            if (entity != null) {
               runOld.accept(entity);
               return entity;
            } else {
               throw new IllegalArgumentException("Cannot spawn an entity for " + clazz.getName());
            }
         }
      } else if (CraftEntity.class.isAssignableFrom(clazz)) {
         throw new IllegalArgumentException(String.format("Cannot spawn an entity from its CraftBukkit implementation class '%s' use the Bukkit class instead. You can get the Bukkit representation via Entity#getType()#getEntityClass()", clazz.getName()));
      } else {
         throw new IllegalArgumentException("Cannot spawn an entity for " + clazz.getName());
      }
   }
}
