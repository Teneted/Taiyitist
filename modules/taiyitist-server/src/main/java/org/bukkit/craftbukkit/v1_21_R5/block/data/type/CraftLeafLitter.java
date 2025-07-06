package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.type.LeafLitter;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public abstract class CraftLeafLitter extends CraftBlockData implements LeafLitter {
   private static final IntegerProperty SEGMENT_AMOUNT = getInteger("segment_amount");

   public int getSegmentAmount() {
      return (Integer)this.get(SEGMENT_AMOUNT);
   }

   public void setSegmentAmount(int segment_amount) {
      this.set(SEGMENT_AMOUNT, segment_amount);
   }

   public int getMaximumSegmentAmount() {
      return getMax(SEGMENT_AMOUNT);
   }
}
