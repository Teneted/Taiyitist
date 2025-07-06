package org.bukkit.craftbukkit.block.data.type;

import org.bukkit.block.data.type.TestBlock;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public abstract class CraftTestBlock extends CraftBlockData implements TestBlock {
   private static final CraftBlockStateEnum<?, TestBlock.Mode> MODE = getEnum("mode", TestBlock.Mode.class);

   public TestBlock.Mode getMode() {
      return (TestBlock.Mode)this.get(MODE);
   }

   public void setMode(TestBlock.Mode mode) {
      this.set(MODE, mode);
   }
}
