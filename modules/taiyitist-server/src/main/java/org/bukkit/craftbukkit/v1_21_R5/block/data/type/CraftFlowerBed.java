package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.type.FlowerBed;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public abstract class CraftFlowerBed extends CraftBlockData implements FlowerBed {
   private static final IntegerProperty FLOWER_AMOUNT = getInteger("flower_amount");

   public int getFlowerAmount() {
      return (Integer)this.get(FLOWER_AMOUNT);
   }

   public void setFlowerAmount(int flower_amount) {
      this.set(FLOWER_AMOUNT, flower_amount);
   }

   public int getMaximumFlowerAmount() {
      return getMax(FLOWER_AMOUNT);
   }
}
