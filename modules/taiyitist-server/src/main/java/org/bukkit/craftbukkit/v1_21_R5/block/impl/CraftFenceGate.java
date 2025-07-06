package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Gate;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftFenceGate extends CraftBlockData implements Gate, Directional, Openable, Powerable {
   private static final BooleanProperty IN_WALL = getBoolean(FenceGateBlock.class, "in_wall");
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(FenceGateBlock.class, "facing", BlockFace.class);
   private static final BooleanProperty OPEN = getBoolean(FenceGateBlock.class, "open");
   private static final BooleanProperty POWERED = getBoolean(FenceGateBlock.class, "powered");

   public CraftFenceGate() {
   }

   public CraftFenceGate(BlockState state) {
      super(state);
   }

   public boolean isInWall() {
      return (Boolean)this.get(IN_WALL);
   }

   public void setInWall(boolean inWall) {
      this.set(IN_WALL, inWall);
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
