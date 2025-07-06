package org.bukkit.craftbukkit.block.impl;

import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.data.type.StructureBlock;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftStructure extends CraftBlockData implements StructureBlock {
   private static final CraftBlockStateEnum<?, StructureBlock.Mode> MODE = getEnum(net.minecraft.world.level.block.StructureBlock.class, "mode", StructureBlock.Mode.class);

   public CraftStructure() {
   }

   public CraftStructure(BlockState state) {
      super(state);
   }

   public StructureBlock.Mode getMode() {
      return (StructureBlock.Mode)this.get(MODE);
   }

   public void setMode(StructureBlock.Mode mode) {
      this.set(MODE, mode);
   }
}
