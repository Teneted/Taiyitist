package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.GrindstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.type.Grindstone;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftGrindstone extends CraftBlockData implements Grindstone, Directional, FaceAttachable {
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(GrindstoneBlock.class, "facing", BlockFace.class);
   private static final CraftBlockStateEnum<?, FaceAttachable.AttachedFace> ATTACH_FACE = getEnum(GrindstoneBlock.class, "face", FaceAttachable.AttachedFace.class);

   public CraftGrindstone() {
   }

   public CraftGrindstone(BlockState state) {
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

   public FaceAttachable.AttachedFace getAttachedFace() {
      return (FaceAttachable.AttachedFace)this.get(ATTACH_FACE);
   }

   public void setAttachedFace(FaceAttachable.AttachedFace face) {
      this.set(ATTACH_FACE, face);
   }
}
