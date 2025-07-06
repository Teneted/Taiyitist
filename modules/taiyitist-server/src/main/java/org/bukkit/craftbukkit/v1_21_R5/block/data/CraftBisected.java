package org.bukkit.craftbukkit.v1_21_R5.block.data;

import org.bukkit.block.data.Bisected;

public class CraftBisected extends CraftBlockData implements Bisected {
   private static final CraftBlockStateEnum<?, Bisected.Half> HALF = getEnum("half", Bisected.Half.class);

   public Bisected.Half getHalf() {
      return (Bisected.Half)this.get(HALF);
   }

   public void setHalf(Bisected.Half half) {
      this.set(HALF, half);
   }
}
