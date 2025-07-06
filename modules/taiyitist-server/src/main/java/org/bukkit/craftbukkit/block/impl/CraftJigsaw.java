package org.bukkit.craftbukkit.block.impl;

import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.data.type.Jigsaw;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftJigsaw extends CraftBlockData implements Jigsaw {
   private static final CraftBlockStateEnum<?, Jigsaw.Orientation> ORIENTATION = getEnum(JigsawBlock.class, "orientation", Jigsaw.Orientation.class);

   public CraftJigsaw() {
   }

   public CraftJigsaw(BlockState state) {
      super(state);
   }

   public Jigsaw.Orientation getOrientation() {
      return (Jigsaw.Orientation)this.get(ORIENTATION);
   }

   public void setOrientation(Jigsaw.Orientation orientation) {
      this.set(ORIENTATION, orientation);
   }
}
