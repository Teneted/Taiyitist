package org.bukkit.craftbukkit.block.data.type;

import org.bukkit.block.data.type.StructureBlock;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public abstract class CraftStructureBlock extends CraftBlockData implements StructureBlock {
   private static final CraftBlockStateEnum<?, StructureBlock.Mode> MODE = getEnum("mode", StructureBlock.Mode.class);

   public StructureBlock.Mode getMode() {
      return (StructureBlock.Mode)this.get(MODE);
   }

   public void setMode(StructureBlock.Mode mode) {
      this.set(MODE, mode);
   }
}
