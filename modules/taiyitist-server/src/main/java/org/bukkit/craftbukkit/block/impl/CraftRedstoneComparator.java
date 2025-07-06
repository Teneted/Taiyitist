package org.bukkit.craftbukkit.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Comparator;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftRedstoneComparator extends CraftBlockData implements Comparator, Directional, Powerable {
   private static final CraftBlockStateEnum<?, Comparator.Mode> MODE = getEnum(ComparatorBlock.class, "mode", Comparator.Mode.class);
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(ComparatorBlock.class, "facing", BlockFace.class);
   private static final BooleanProperty POWERED = getBoolean(ComparatorBlock.class, "powered");

   public CraftRedstoneComparator() {
   }

   public CraftRedstoneComparator(BlockState state) {
      super(state);
   }

   public Comparator.Mode getMode() {
      return (Comparator.Mode)this.get(MODE);
   }

   public void setMode(Comparator.Mode mode) {
      this.set(MODE, mode);
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
