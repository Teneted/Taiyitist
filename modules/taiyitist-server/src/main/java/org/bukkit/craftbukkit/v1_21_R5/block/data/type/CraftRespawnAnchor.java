package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public abstract class CraftRespawnAnchor extends CraftBlockData implements RespawnAnchor {
   private static final IntegerProperty CHARGES = getInteger("charges");

   public int getCharges() {
      return (Integer)this.get(CHARGES);
   }

   public void setCharges(int charges) {
      this.set(CHARGES, charges);
   }

   public int getMaximumCharges() {
      return getMax(CHARGES);
   }
}
