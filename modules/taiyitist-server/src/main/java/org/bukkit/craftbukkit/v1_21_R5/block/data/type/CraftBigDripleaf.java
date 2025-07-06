package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import org.bukkit.block.data.type.BigDripleaf;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public abstract class CraftBigDripleaf extends CraftBlockData implements BigDripleaf {
   private static final CraftBlockStateEnum<?, BigDripleaf.Tilt> TILT = getEnum("tilt", BigDripleaf.Tilt.class);

   public BigDripleaf.Tilt getTilt() {
      return (BigDripleaf.Tilt)this.get(TILT);
   }

   public void setTilt(BigDripleaf.Tilt tilt) {
      this.set(TILT, tilt);
   }
}
