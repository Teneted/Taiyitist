package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public abstract class CraftRedstoneWire extends CraftBlockData implements RedstoneWire {
   private static final CraftBlockStateEnum<?, RedstoneWire.Connection> NORTH = getEnum("north", RedstoneWire.Connection.class);
   private static final CraftBlockStateEnum<?, RedstoneWire.Connection> EAST = getEnum("east", RedstoneWire.Connection.class);
   private static final CraftBlockStateEnum<?, RedstoneWire.Connection> SOUTH = getEnum("south", RedstoneWire.Connection.class);
   private static final CraftBlockStateEnum<?, RedstoneWire.Connection> WEST = getEnum("west", RedstoneWire.Connection.class);

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
}
