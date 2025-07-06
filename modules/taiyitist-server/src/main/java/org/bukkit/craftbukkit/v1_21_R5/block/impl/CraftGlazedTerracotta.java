package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.GlazedTerracottaBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftGlazedTerracotta extends CraftBlockData implements Directional {
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(GlazedTerracottaBlock.class, "facing", BlockFace.class);

   public CraftGlazedTerracotta() {
   }

   public CraftGlazedTerracotta(BlockState state) {
      super(state);
   }

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
