package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.HangingMoss;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public abstract class CraftHangingMoss extends CraftBlockData implements HangingMoss {
   private static final BooleanProperty TIP = getBoolean("tip");

   public boolean isTip() {
      return (Boolean)this.get(TIP);
   }

   public void setTip(boolean tip) {
      this.set(TIP, tip);
   }
}
