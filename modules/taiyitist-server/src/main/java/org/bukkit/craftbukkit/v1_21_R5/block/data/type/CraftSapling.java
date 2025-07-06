package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public abstract class CraftSapling extends CraftBlockData implements Sapling {
   private static final IntegerProperty STAGE = getInteger("stage");

   public int getStage() {
      return (Integer)this.get(STAGE);
   }

   public void setStage(int stage) {
      this.set(STAGE, stage);
   }

   public int getMaximumStage() {
      return getMax(STAGE);
   }
}
