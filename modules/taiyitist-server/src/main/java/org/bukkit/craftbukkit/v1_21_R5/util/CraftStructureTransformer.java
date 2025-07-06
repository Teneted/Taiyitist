package org.bukkit.craftbukkit.v1_21_R5.util;

import java.util.Collection;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_21_R5.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_21_R5.generator.CraftLimitedRegion;
import org.bukkit.craftbukkit.v1_21_R5.generator.structure.CraftStructure;
import org.bukkit.event.world.AsyncStructureGenerateEvent;
import org.bukkit.util.BlockTransformer;
import org.bukkit.util.EntityTransformer;

public class CraftStructureTransformer {
   private CraftLimitedRegion limitedRegion;
   private BlockTransformer[] blockTransformers;
   private EntityTransformer[] entityTransformers;

   public CraftStructureTransformer(AsyncStructureGenerateEvent.Cause cause, WorldGenLevel generatoraccessseed, StructureManager structuremanager, Structure structure, BoundingBox structureboundingbox, ChunkPos chunkcoordintpair) {
      AsyncStructureGenerateEvent event = new AsyncStructureGenerateEvent(structuremanager.level.getMinecraftWorld().getWorld(), !Bukkit.isPrimaryThread(), cause, CraftStructure.minecraftToBukkit(structure), new org.bukkit.util.BoundingBox((double)structureboundingbox.minX(), (double)structureboundingbox.minY(), (double)structureboundingbox.minZ(), (double)structureboundingbox.maxX(), (double)structureboundingbox.maxY(), (double)structureboundingbox.maxZ()), chunkcoordintpair.x, chunkcoordintpair.z);
      Bukkit.getPluginManager().callEvent(event);
      this.blockTransformers = (BlockTransformer[])event.getBlockTransformers().values().toArray((x$0) -> {
         return new BlockTransformer[x$0];
      });
      this.entityTransformers = (EntityTransformer[])event.getEntityTransformers().values().toArray((x$0) -> {
         return new EntityTransformer[x$0];
      });
      this.limitedRegion = new CraftLimitedRegion(generatoraccessseed, chunkcoordintpair);
   }

   public CraftStructureTransformer(WorldGenLevel generatoraccessseed, ChunkPos chunkcoordintpair, Collection<BlockTransformer> blockTransformers, Collection<EntityTransformer> entityTransformers) {
      this.blockTransformers = (BlockTransformer[])blockTransformers.toArray((x$0) -> {
         return new BlockTransformer[x$0];
      });
      this.entityTransformers = (EntityTransformer[])entityTransformers.toArray((x$0) -> {
         return new EntityTransformer[x$0];
      });
      this.limitedRegion = new CraftLimitedRegion(generatoraccessseed, chunkcoordintpair);
   }

   public boolean transformEntity(Entity entity) {
      EntityTransformer[] transformers = this.entityTransformers;
      if (transformers != null && transformers.length != 0) {
         CraftLimitedRegion region = this.limitedRegion;
         if (region == null) {
            return true;
         } else {
            entity.generation = true;
            CraftEntity craftEntity = entity.getBukkitEntity();
            int x = entity.getBlockX();
            int y = entity.getBlockY();
            int z = entity.getBlockZ();
            boolean allowedToSpawn = true;
            EntityTransformer[] var9 = transformers;
            int var10 = transformers.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               EntityTransformer transformer = var9[var11];
               allowedToSpawn = transformer.transform(region, x, y, z, craftEntity, allowedToSpawn);
            }

            return allowedToSpawn;
         }
      } else {
         return true;
      }
   }

   public boolean canTransformBlocks() {
      return this.blockTransformers != null && this.blockTransformers.length != 0 && this.limitedRegion != null;
   }

   public CraftBlockState transformCraftState(CraftBlockState originalState) {
      BlockTransformer[] transformers = this.blockTransformers;
      if (transformers != null && transformers.length != 0) {
         CraftLimitedRegion region = this.limitedRegion;
         if (region == null) {
            return originalState;
         } else {
            originalState.setWorldHandle(region.getHandle());
            BlockPos position = originalState.getPosition();
            BlockState blockState = originalState.copy();
            CraftTransformationState transformationState = new CraftTransformationState(originalState, region.getBlockState(position.getX(), position.getY(), position.getZ()));
            BlockTransformer[] var7 = transformers;
            int var8 = transformers.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               BlockTransformer transformer = var7[var9];
               blockState = (BlockState)Objects.requireNonNull(transformer.transform(region, position.getX(), position.getY(), position.getZ(), (BlockState)blockState, transformationState), "BlockState can't be null");
               transformationState.destroyCopies();
            }

            return (CraftBlockState)blockState;
         }
      } else {
         return originalState;
      }
   }

   public void discard() {
      this.limitedRegion.saveEntities();
      this.limitedRegion.breakLink();
      this.limitedRegion = null;
      this.blockTransformers = null;
      this.entityTransformers = null;
   }

   private static class CraftTransformationState implements BlockTransformer.TransformationState {
      private final BlockState original;
      private final BlockState world;
      private BlockState originalCopy;
      private BlockState worldCopy;

      private CraftTransformationState(BlockState original, BlockState world) {
         this.original = original;
         this.world = world;
      }

      public BlockState getOriginal() {
         return this.originalCopy != null ? this.originalCopy : (this.originalCopy = this.original.copy());
      }

      public BlockState getWorld() {
         return this.worldCopy != null ? this.worldCopy : (this.worldCopy = this.world.copy());
      }

      private void destroyCopies() {
         this.originalCopy = null;
         this.worldCopy = null;
      }
   }
}
