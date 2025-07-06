package org.bukkit.craftbukkit.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.LeafLitterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.LeafLitter;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftLeafLitter extends CraftBlockData implements LeafLitter, Directional {
   private static final IntegerProperty SEGMENT_AMOUNT = getInteger(LeafLitterBlock.class, "segment_amount");
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(LeafLitterBlock.class, "facing", BlockFace.class);

   public CraftLeafLitter() {
   }

   public CraftLeafLitter(BlockState state) {
      super(state);
   }

   public int getSegmentAmount() {
      return (Integer)this.get(SEGMENT_AMOUNT);
   }

   public void setSegmentAmount(int segment_amount) {
      this.set(SEGMENT_AMOUNT, segment_amount);
   }

   public int getMaximumSegmentAmount() {
      return getMax(SEGMENT_AMOUNT);
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
}
