package org.bukkit.craftbukkit.block.impl;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftRedstoneWire extends CraftBlockData implements RedstoneWire, AnaloguePowerable {
   private static final CraftBlockStateEnum<?, RedstoneWire.Connection> NORTH = getEnum(RedStoneWireBlock.class, "north", RedstoneWire.Connection.class);
   private static final CraftBlockStateEnum<?, RedstoneWire.Connection> EAST = getEnum(RedStoneWireBlock.class, "east", RedstoneWire.Connection.class);
   private static final CraftBlockStateEnum<?, RedstoneWire.Connection> SOUTH = getEnum(RedStoneWireBlock.class, "south", RedstoneWire.Connection.class);
   private static final CraftBlockStateEnum<?, RedstoneWire.Connection> WEST = getEnum(RedStoneWireBlock.class, "west", RedstoneWire.Connection.class);
   private static final IntegerProperty POWER = getInteger(RedStoneWireBlock.class, "power");

   public CraftRedstoneWire() {
   }

   public CraftRedstoneWire(BlockState state) {
      super(state);
   }

   public RedstoneWire.Connection getFace(BlockFace face) {
      switch (face) {
         case NORTH -> {
            return (RedstoneWire.Connection)this.get(NORTH);
         }
         case EAST -> {
            return (RedstoneWire.Connection)this.get(EAST);
         }
         case SOUTH -> {
            return (RedstoneWire.Connection)this.get(SOUTH);
         }
         case WEST -> {
            return (RedstoneWire.Connection)this.get(WEST);
         }
         default -> throw new IllegalArgumentException("Cannot have face " + String.valueOf(face));
      }
   }

   public void setFace(BlockFace face, RedstoneWire.Connection connection) {
      switch (face) {
         case NORTH -> this.set(NORTH, connection);
         case EAST -> this.set(EAST, connection);
         case SOUTH -> this.set(SOUTH, connection);
         case WEST -> this.set(WEST, connection);
         default -> throw new IllegalArgumentException("Cannot have face " + String.valueOf(face));
      }

   }

   public Set<BlockFace> getAllowedFaces() {
      return ImmutableSet.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);
   }

   public int getPower() {
      return (Integer)this.get(POWER);
   }

   public void setPower(int power) {
      this.set(POWER, power);
   }

   public int getMaximumPower() {
      return getMax(POWER);
   }
}
