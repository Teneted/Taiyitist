package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rotatable;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public final class CraftBanner extends CraftBlockData implements Rotatable {
   private static final IntegerProperty ROTATION = getInteger(BannerBlock.class, "rotation");

   public CraftBanner() {
   }

   public CraftBanner(BlockState state) {
      super(state);
   }

   public BlockFace getRotation() {
      int data = (Integer)this.get(ROTATION);
      switch (data) {
         case 0 -> {
            return BlockFace.SOUTH;
         }
         case 1 -> {
            return BlockFace.SOUTH_SOUTH_WEST;
         }
         case 2 -> {
            return BlockFace.SOUTH_WEST;
         }
         case 3 -> {
            return BlockFace.WEST_SOUTH_WEST;
         }
         case 4 -> {
            return BlockFace.WEST;
         }
         case 5 -> {
            return BlockFace.WEST_NORTH_WEST;
         }
         case 6 -> {
            return BlockFace.NORTH_WEST;
         }
         case 7 -> {
            return BlockFace.NORTH_NORTH_WEST;
         }
         case 8 -> {
            return BlockFace.NORTH;
         }
         case 9 -> {
            return BlockFace.NORTH_NORTH_EAST;
         }
         case 10 -> {
            return BlockFace.NORTH_EAST;
         }
         case 11 -> {
            return BlockFace.EAST_NORTH_EAST;
         }
         case 12 -> {
            return BlockFace.EAST;
         }
         case 13 -> {
            return BlockFace.EAST_SOUTH_EAST;
         }
         case 14 -> {
            return BlockFace.SOUTH_EAST;
         }
         case 15 -> {
            return BlockFace.SOUTH_SOUTH_EAST;
         }
         default -> throw new IllegalArgumentException("Unknown rotation " + data);
      }
   }

   public void setRotation(BlockFace rotation) {
      byte val;
      switch (rotation) {
         case SOUTH -> val = 0;
         case SOUTH_SOUTH_WEST -> val = 1;
         case SOUTH_WEST -> val = 2;
         case WEST_SOUTH_WEST -> val = 3;
         case WEST -> val = 4;
         case WEST_NORTH_WEST -> val = 5;
         case NORTH_WEST -> val = 6;
         case NORTH_NORTH_WEST -> val = 7;
         case NORTH -> val = 8;
         case NORTH_NORTH_EAST -> val = 9;
         case NORTH_EAST -> val = 10;
         case EAST_NORTH_EAST -> val = 11;
         case EAST -> val = 12;
         case EAST_SOUTH_EAST -> val = 13;
         case SOUTH_EAST -> val = 14;
         case SOUTH_SOUTH_EAST -> val = 15;
         default -> throw new IllegalArgumentException("Illegal rotation " + String.valueOf(rotation));
      }

      this.set(ROTATION, Integer.valueOf(val));
   }
}
