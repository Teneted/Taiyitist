package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.ResinClump;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public final class CraftMultiface extends CraftBlockData implements ResinClump, MultipleFacing, Waterlogged {
   private static final BooleanProperty[] FACES = new BooleanProperty[]{getBoolean(MultifaceBlock.class, "north", true), getBoolean(MultifaceBlock.class, "east", true), getBoolean(MultifaceBlock.class, "south", true), getBoolean(MultifaceBlock.class, "west", true), getBoolean(MultifaceBlock.class, "up", true), getBoolean(MultifaceBlock.class, "down", true)};
   private static final BooleanProperty WATERLOGGED = getBoolean(MultifaceBlock.class, "waterlogged");

   public CraftMultiface() {
   }

   public CraftMultiface(BlockState state) {
      super(state);
   }

   public boolean hasFace(BlockFace face) {
      BooleanProperty state = FACES[face.ordinal()];
      if (state == null) {
         throw new IllegalArgumentException("Non-allowed face " + String.valueOf(face) + ". Check MultipleFacing.getAllowedFaces.");
      } else {
         return (Boolean)this.get(state);
      }
   }

   public void setFace(BlockFace face, boolean has) {
      BooleanProperty state = FACES[face.ordinal()];
      if (state == null) {
         throw new IllegalArgumentException("Non-allowed face " + String.valueOf(face) + ". Check MultipleFacing.getAllowedFaces.");
      } else {
         this.set(state, has);
      }
   }

   public Set<BlockFace> getFaces() {
      ImmutableSet.Builder<BlockFace> faces = ImmutableSet.builder();

      for(int i = 0; i < FACES.length; ++i) {
         if (FACES[i] != null && (Boolean)this.get(FACES[i])) {
            faces.add(BlockFace.values()[i]);
         }
      }

      return faces.build();
   }

   public Set<BlockFace> getAllowedFaces() {
      ImmutableSet.Builder<BlockFace> faces = ImmutableSet.builder();

      for(int i = 0; i < FACES.length; ++i) {
         if (FACES[i] != null) {
            faces.add(BlockFace.values()[i]);
         }
      }

      return faces.build();
   }

   public boolean isWaterlogged() {
      return (Boolean)this.get(WATERLOGGED);
   }

   public void setWaterlogged(boolean waterlogged) {
      this.set(WATERLOGGED, waterlogged);
   }
}
