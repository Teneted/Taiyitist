package org.bukkit.craftbukkit.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Repeater;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftRepeater extends CraftBlockData implements Repeater, Directional, Powerable {
   private static final IntegerProperty DELAY = getInteger(RepeaterBlock.class, "delay");
   private static final BooleanProperty LOCKED = getBoolean(RepeaterBlock.class, "locked");
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(RepeaterBlock.class, "facing", BlockFace.class);
   private static final BooleanProperty POWERED = getBoolean(RepeaterBlock.class, "powered");

   public CraftRepeater() {
   }

   public CraftRepeater(BlockState state) {
      super(state);
   }

   public int getDelay() {
      return (Integer)this.get(DELAY);
   }

   public void setDelay(int delay) {
      this.set(DELAY, delay);
   }

   public int getMinimumDelay() {
      return getMin(DELAY);
   }

   public int getMaximumDelay() {
      return getMax(DELAY);
   }

   public boolean isLocked() {
      return (Boolean)this.get(LOCKED);
   }

   public void setLocked(boolean locked) {
      this.set(LOCKED, locked);
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

   public boolean isPowered() {
      return (Boolean)this.get(POWERED);
   }

   public void setPowered(boolean powered) {
      this.set(POWERED, powered);
   }
}
