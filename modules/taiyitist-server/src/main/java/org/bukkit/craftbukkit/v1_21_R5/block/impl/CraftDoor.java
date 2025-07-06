package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Door;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftDoor extends CraftBlockData implements Door, Bisected, Directional, Openable, Powerable {
   private static final CraftBlockStateEnum<?, Door.Hinge> HINGE = getEnum(DoorBlock.class, "hinge", Door.Hinge.class);
   private static final CraftBlockStateEnum<?, Bisected.Half> HALF = getEnum(DoorBlock.class, "half", Bisected.Half.class);
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(DoorBlock.class, "facing", BlockFace.class);
   private static final BooleanProperty OPEN = getBoolean(DoorBlock.class, "open");
   private static final BooleanProperty POWERED = getBoolean(DoorBlock.class, "powered");

   public CraftDoor() {
   }

   public CraftDoor(BlockState state) {
      super(state);
   }

   public Door.Hinge getHinge() {
      return (Door.Hinge)this.get(HINGE);
   }

   public void setHinge(Door.Hinge hinge) {
      this.set(HINGE, hinge);
   }

   public Bisected.Half getHalf() {
      return (Bisected.Half)this.get(HALF);
   }

   public void setHalf(Bisected.Half half) {
      this.set(HALF, half);
   }

   public BlockFace getFacing() {
      return (BlockFace)this.get(FACING);
   }

   public void setFacing(BlockFace facing) {
      this.set(FACING, facing);
   }

   public Set<BlockFace> getFaces() {
      return this.getValues(FACING);
   }

   public boolean isOpen() {
      return (Boolean)this.get(OPEN);
   }

   public void setOpen(boolean open) {
      this.set(OPEN, open);
   }

   public boolean isPowered() {
      return (Boolean)this.get(POWERED);
   }

   public void setPowered(boolean powered) {
      this.set(POWERED, powered);
   }
}
