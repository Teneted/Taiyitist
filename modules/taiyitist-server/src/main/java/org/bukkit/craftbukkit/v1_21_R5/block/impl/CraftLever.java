package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Switch;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftLever extends CraftBlockData implements Switch, Directional, FaceAttachable, Powerable {
   private static final CraftBlockStateEnum<?, Switch.Face> FACE = getEnum(LeverBlock.class, "face", Switch.Face.class);
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(LeverBlock.class, "facing", BlockFace.class);
   private static final CraftBlockStateEnum<?, FaceAttachable.AttachedFace> ATTACH_FACE = getEnum(LeverBlock.class, "face", FaceAttachable.AttachedFace.class);
   private static final BooleanProperty POWERED = getBoolean(LeverBlock.class, "powered");

   public CraftLever() {
   }

   public CraftLever(BlockState state) {
      super(state);
   }

   public Switch.Face getFace() {
      return (Switch.Face)this.get(FACE);
   }

   public void setFace(Switch.Face face) {
      this.set(FACE, face);
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

   public boolean isPowered() {
      return (Boolean)this.get(POWERED);
   }

   public void setPowered(boolean powered) {
      this.set(POWERED, powered);
   }
}
