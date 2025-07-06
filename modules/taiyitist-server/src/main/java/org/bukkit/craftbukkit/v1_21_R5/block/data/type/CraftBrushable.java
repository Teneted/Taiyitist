package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.Brushable;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public abstract class CraftBrushable extends CraftBlockData implements Brushable {
   private static final IntegerProperty DUSTED = getInteger("dusted");

   public int getDusted() {
      return (Integer)this.get(DUSTED);
   }

   public void setDusted(int dusted) {
      this.set(DUSTED, dusted);
   }

   public int getMaximumDusted() {
      return getMax(DUSTED);
   }
}
