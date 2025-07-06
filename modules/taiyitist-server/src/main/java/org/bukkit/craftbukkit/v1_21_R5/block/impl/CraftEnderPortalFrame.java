package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftEnderPortalFrame extends CraftBlockData implements EndPortalFrame, Directional {
   private static final BooleanProperty EYE = getBoolean(EndPortalFrameBlock.class, "eye");
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(EndPortalFrameBlock.class, "facing", BlockFace.class);

   public CraftEnderPortalFrame() {
   }

   public CraftEnderPortalFrame(BlockState state) {
      super(state);
   }

   public boolean hasEye() {
      return (Boolean)this.get(EYE);
   }

   public void setEye(boolean eye) {
      this.set(EYE, eye);
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
