package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import org.bukkit.block.data.type.Comparator;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public abstract class CraftComparator extends CraftBlockData implements Comparator {
   private static final CraftBlockStateEnum<?, Comparator.Mode> MODE = getEnum("mode", Comparator.Mode.class);

   public Comparator.Mode getMode() {
      return (Comparator.Mode)this.get(MODE);
   }

   public void setMode(Comparator.Mode mode) {
      this.set(MODE, mode);
   }
}
