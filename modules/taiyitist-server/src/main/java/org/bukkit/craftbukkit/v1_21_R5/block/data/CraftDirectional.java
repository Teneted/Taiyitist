package org.bukkit.craftbukkit.v1_21_R5.block.data;

import java.util.Set;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;

public abstract class CraftDirectional extends CraftBlockData implements Directional {
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum("facing", BlockFace.class);

   public BlockFace getFacing() {
      return (BlockFace)this.get(FACING);
   }

   public void setFacing(BlockFace facing) {
      this.set(FACING, facing);
   }

   public Set<BlockFace> getFaces() {
      return this.getValues(FACING);
   }
}
