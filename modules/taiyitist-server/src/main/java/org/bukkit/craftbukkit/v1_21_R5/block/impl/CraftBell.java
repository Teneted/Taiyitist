package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Bell;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftBell extends CraftBlockData implements Bell, Directional, Powerable {
   private static final CraftBlockStateEnum<?, Bell.Attachment> ATTACHMENT = getEnum(BellBlock.class, "attachment", Bell.Attachment.class);
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(BellBlock.class, "facing", BlockFace.class);
   private static final BooleanProperty POWERED = getBoolean(BellBlock.class, "powered");

   public CraftBell() {
   }

   public CraftBell(BlockState state) {
      super(state);
   }

   public Bell.Attachment getAttachment() {
      return (Bell.Attachment)this.get(ATTACHMENT);
   }

   public void setAttachment(Bell.Attachment leaves) {
      this.set(ATTACHMENT, leaves);
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

   public boolean isPowered() {
      return (Boolean)this.get(POWERED);
   }

   public void setPowered(boolean powered) {
      this.set(POWERED, powered);
   }
}
