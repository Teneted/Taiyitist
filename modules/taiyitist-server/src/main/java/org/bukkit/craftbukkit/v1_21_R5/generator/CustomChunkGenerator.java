package org.bukkit.craftbukkit.v1_21_R5.generator;

import com.google.common.base.Preconditions;
import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_21_R5.CraftHeightMap;
import org.bukkit.craftbukkit.v1_21_R5.block.CraftBiome;
import org.bukkit.craftbukkit.v1_21_R5.util.RandomSourceWrapper;

public class CustomChunkGenerator extends InternalChunkGenerator {
   private final ChunkGenerator delegate;
   private final org.bukkit.generator.ChunkGenerator generator;
   private final ServerLevel world;
   private final Random random = new Random();
   private boolean newApi;
   private boolean implementBaseHeight = true;

   public CustomChunkGenerator(ServerLevel world, ChunkGenerator delegate, org.bukkit.generator.ChunkGenerator generator) {
      super(delegate.getBiomeSource(), delegate.generationSettingsGetter);
      this.world = world;
      this.delegate = delegate;
      this.generator = generator;
   }

   public ChunkGenerator getDelegate() {
      return this.delegate;
   }

   private static WorldgenRandom getSeededRandom() {
      return new WorldgenRandom(new LegacyRandomSource(0L));
   }

   public BiomeSource getBiomeSource() {
      return this.delegate.getBiomeSource();
   }

   public int getMinY() {
      return this.delegate.getMinY();
   }

   public int getSeaLevel() {
      return this.delegate.getSeaLevel();
   }

   public void createStructures(RegistryAccess iregistrycustom, ChunkGeneratorStructureState chunkgeneratorstructurestate, StructureManager structuremanager, ChunkAccess ichunkaccess, StructureTemplateManager structuretemplatemanager, ResourceKey<Level> resourcekey) {
      WorldgenRandom random = getSeededRandom();
      int x = ichunkaccess.getPos().x;
      int z = ichunkaccess.getPos().z;
      random.setSeed(Mth.getSeed(x, "should-structures".hashCode(), z) ^ this.world.getSeed());
      if (this.generator.shouldGenerateStructures(this.world.getWorld(), new RandomSourceWrapper.RandomWrapper(random), x, z)) {
         super.createStructures(iregistrycustom, chunkgeneratorstructurestate, structuremanager, ichunkaccess, structuretemplatemanager, resourcekey);
      }

   }

   public void buildSurface(WorldGenRegion regionlimitedworldaccess, StructureManager structuremanager, RandomState randomstate, ChunkAccess ichunkaccess) {
      WorldgenRandom random = getSeededRandom();
      int x = ichunkaccess.getPos().x;
      int z = ichunkaccess.getPos().z;
      random.setSeed(Mth.getSeed(x, "should-surface".hashCode(), z) ^ regionlimitedworldaccess.getSeed());
      if (this.generator.shouldGenerateSurface(this.world.getWorld(), new RandomSourceWrapper.RandomWrapper(random), x, z)) {
         this.delegate.buildSurface(regionlimitedworldaccess, structuremanager, randomstate, ichunkaccess);
      }

      CraftChunkData chunkData = new CraftChunkData(this.world.getWorld(), ichunkaccess);
      random.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
      this.generator.generateSurface(this.world.getWorld(), new RandomSourceWrapper.RandomWrapper(random), x, z, chunkData);
      if (this.generator.shouldGenerateBedrock()) {
         random = getSeededRandom();
         random.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
      }

      random = getSeededRandom();
      random.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
      this.generator.generateBedrock(this.world.getWorld(), new RandomSourceWrapper.RandomWrapper(random), x, z, chunkData);
      chunkData.breakLink();
      if (!this.newApi) {
         this.random.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
         CustomBiomeGrid biomegrid = new CustomBiomeGrid(ichunkaccess);

         org.bukkit.generator.ChunkGenerator.ChunkData data;
         try {
            if (this.generator.isParallelCapable()) {
               data = this.generator.generateChunkData(this.world.getWorld(), this.random, x, z, biomegrid);
            } else {
               synchronized(this) {
                  data = this.generator.generateChunkData(this.world.getWorld(), this.random, x, z, biomegrid);
               }
            }
         } catch (UnsupportedOperationException var23) {
            this.newApi = true;
            return;
         }

         Preconditions.checkArgument(data instanceof OldCraftChunkData, "Plugins must use createChunkData(World) rather than implementing ChunkData: %s", data);
         OldCraftChunkData craftData = (OldCraftChunkData)data;
         LevelChunkSection[] sections = craftData.getRawChunkData();
         LevelChunkSection[] csect = ichunkaccess.getSections();
         int scnt = Math.min(csect.length, sections.length);

         int biomeX;
         int biomeY;
         for(int sec = 0; sec < scnt; ++sec) {
            if (sections[sec] != null) {
               LevelChunkSection section = sections[sec];
               LevelChunkSection oldSection = csect[sec];

               for(biomeX = 0; biomeX < 4; ++biomeX) {
                  for(biomeY = 0; biomeY < 4; ++biomeY) {
                     for(int biomeZ = 0; biomeZ < 4; ++biomeZ) {
                        section.setBiome(biomeX, biomeY, biomeZ, oldSection.getNoiseBiome(biomeX, biomeY, biomeZ));
                     }
                  }
               }

               csect[sec] = section;
            }
         }

         if (craftData.getTiles() != null) {
            Iterator var24 = craftData.getTiles().iterator();

            while(var24.hasNext()) {
               BlockPos pos = (BlockPos)var24.next();
               int tx = pos.getX();
               biomeX = pos.getY();
               biomeY = pos.getZ();
               BlockState block = craftData.getTypeId(tx, biomeX, biomeY);
               if (block.hasBlockEntity()) {
                  BlockEntity tile = ((EntityBlock)block.getBlock()).newBlockEntity(new BlockPos((x << 4) + tx, biomeX, (z << 4) + biomeY), block);
                  ichunkaccess.setBlockEntity(tile);
               }
            }
         }

      }
   }

   public void applyCarvers(WorldGenRegion regionlimitedworldaccess, long seed, RandomState randomstate, BiomeManager biomemanager, StructureManager structuremanager, ChunkAccess ichunkaccess) {
      WorldgenRandom random = getSeededRandom();
      int x = ichunkaccess.getPos().x;
      int z = ichunkaccess.getPos().z;
      random.setSeed(Mth.getSeed(x, "should-caves".hashCode(), z) ^ regionlimitedworldaccess.getSeed());
      if (this.generator.shouldGenerateCaves(this.world.getWorld(), new RandomSourceWrapper.RandomWrapper(random), x, z)) {
         this.delegate.applyCarvers(regionlimitedworldaccess, seed, randomstate, biomemanager, structuremanager, ichunkaccess);
      }

      CraftChunkData chunkData = new CraftChunkData(this.world.getWorld(), ichunkaccess);
      random.setDecorationSeed(seed, 0, 0);
      this.generator.generateCaves(this.world.getWorld(), new RandomSourceWrapper.RandomWrapper(random), x, z, chunkData);
      chunkData.breakLink();
   }

   public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomstate, StructureManager structuremanager, ChunkAccess ichunkaccess) {
      CompletableFuture<ChunkAccess> future = null;
      WorldgenRandom random = getSeededRandom();
      int x = ichunkaccess.getPos().x;
      int z = ichunkaccess.getPos().z;
      random.setSeed(Mth.getSeed(x, "should-noise".hashCode(), z) ^ this.world.getSeed());
      if (this.generator.shouldGenerateNoise(this.world.getWorld(), new RandomSourceWrapper.RandomWrapper(random), x, z)) {
         future = this.delegate.fillFromNoise(blender, randomstate, structuremanager, ichunkaccess);
      }

      Function<ChunkAccess, ChunkAccess> function = (ichunkaccess1) -> {
         CraftChunkData chunkData = new CraftChunkData(this.world.getWorld(), ichunkaccess1);
         random.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
         this.generator.generateNoise(this.world.getWorld(), new RandomSourceWrapper.RandomWrapper(random), x, z, chunkData);
         chunkData.breakLink();
         return ichunkaccess1;
      };
      return future == null ? CompletableFuture.supplyAsync(() -> {
         return (ChunkAccess)function.apply(ichunkaccess);
      }, Util.backgroundExecutor()) : future.thenApply(function);
   }

   public int getBaseHeight(int i, int j, Heightmap.Types heightmap_type, LevelHeightAccessor levelheightaccessor, RandomState randomstate) {
      if (this.implementBaseHeight) {
         try {
            WorldgenRandom random = getSeededRandom();
            int xChunk = i >> 4;
            int zChunk = j >> 4;
            random.setSeed((long)xChunk * 341873128712L + (long)zChunk * 132897987541L);
            return this.generator.getBaseHeight(this.world.getWorld(), new RandomSourceWrapper.RandomWrapper(random), i, j, CraftHeightMap.fromNMS(heightmap_type));
         } catch (UnsupportedOperationException var9) {
            this.implementBaseHeight = false;
         }
      }

      return this.delegate.getBaseHeight(i, j, heightmap_type, levelheightaccessor, randomstate);
   }

   public void applyBiomeDecoration(WorldGenLevel generatoraccessseed, ChunkAccess ichunkaccess, StructureManager structuremanager) {
      WorldgenRandom random = getSeededRandom();
      int x = ichunkaccess.getPos().x;
      int z = ichunkaccess.getPos().z;
      random.setSeed(Mth.getSeed(x, "should-decoration".hashCode(), z) ^ generatoraccessseed.getSeed());
      super.applyBiomeDecoration(generatoraccessseed, ichunkaccess, structuremanager, this.generator.shouldGenerateDecorations(this.world.getWorld(), new RandomSourceWrapper.RandomWrapper(random), x, z));
   }

   public void addDebugScreenInfo(List<String> list, RandomState randomstate, BlockPos blockposition) {
      this.delegate.addDebugScreenInfo(list, randomstate, blockposition);
   }

   public void spawnOriginalMobs(WorldGenRegion regionlimitedworldaccess) {
      WorldgenRandom random = getSeededRandom();
      int x = regionlimitedworldaccess.getCenter().x;
      int z = regionlimitedworldaccess.getCenter().z;
      random.setSeed(Mth.getSeed(x, "should-mobs".hashCode(), z) ^ regionlimitedworldaccess.getSeed());
      if (this.generator.shouldGenerateMobs(this.world.getWorld(), new RandomSourceWrapper.RandomWrapper(random), x, z)) {
         this.delegate.spawnOriginalMobs(regionlimitedworldaccess);
      }

   }

   public int getSpawnHeight(LevelHeightAccessor levelheightaccessor) {
      return this.delegate.getSpawnHeight(levelheightaccessor);
   }

   public int getGenDepth() {
      return this.delegate.getGenDepth();
   }

   public NoiseColumn getBaseColumn(int i, int j, LevelHeightAccessor levelheightaccessor, RandomState randomstate) {
      return this.delegate.getBaseColumn(i, j, levelheightaccessor, randomstate);
   }

   protected MapCodec<? extends ChunkGenerator> codec() {
      return MapCodec.unit((Supplier)null);
   }

   /** @deprecated */
   @Deprecated
   private class CustomBiomeGrid implements org.bukkit.generator.ChunkGenerator.BiomeGrid {
      private final ChunkAccess biome;

      public CustomBiomeGrid(ChunkAccess biome) {
         this.biome = biome;
      }

      public Biome getBiome(int x, int z) {
         return this.getBiome(x, 0, z);
      }

      public void setBiome(int x, int z, Biome bio) {
         for(int y = CustomChunkGenerator.this.world.getWorld().getMinHeight(); y < CustomChunkGenerator.this.world.getWorld().getMaxHeight(); y += 4) {
            this.setBiome(x, y, z, bio);
         }

      }

      public Biome getBiome(int x, int y, int z) {
         return CraftBiome.minecraftHolderToBukkit(this.biome.getNoiseBiome(x >> 2, y >> 2, z >> 2));
      }

      public void setBiome(int x, int y, int z, Biome bio) {
         Preconditions.checkArgument(bio != Biome.CUSTOM, "Cannot set the biome to %s", bio);
         this.biome.setBiome(x >> 2, y >> 2, z >> 2, CraftBiome.bukkitToMinecraftHolder(bio));
      }
   }
}
