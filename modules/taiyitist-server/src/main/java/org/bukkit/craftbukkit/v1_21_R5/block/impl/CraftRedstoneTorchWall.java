package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.RedstoneWallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.type.RedstoneWallTorch;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftRedstoneTorchWall extends CraftBlockData implements RedstoneWallTorch, Directional, Lightable {
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(RedstoneWallTorchBlock.class, "facing", BlockFace.class);
   private static final BooleanProperty LIT = getBoolean(RedstoneWallTorchBlock.class, "lit");

   public CraftRedstoneTorchWall() {
   }

   public CraftRedstoneTorchWall(BlockState state) {
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

   public boolean isLit() {
      return (Boolean)this.get(LIT);
   }

   public void setLit(boolean lit) {
      this.set(LIT, lit);
   }
}
