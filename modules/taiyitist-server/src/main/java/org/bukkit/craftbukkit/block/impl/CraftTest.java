package org.bukkit.craftbukkit.block.impl;

import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.data.type.TestBlock;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftTest extends CraftBlockData implements TestBlock {
   private static final CraftBlockStateEnum<?, TestBlock.Mode> MODE = getEnum(net.minecraft.world.level.block.TestBlock.class, "mode", TestBlock.Mode.class);

   public CraftTest() {
   }

   public CraftTest(BlockState state) {
      super(state);
   }

   public TestBlock.Mode getMode() {
      return (TestBlock.Mode)this.get(MODE);
   }

   public void setMode(TestBlock.Mode mode) {
      this.set(MODE, mode);
   }
}
