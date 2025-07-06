package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.CommandBlock;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftCommand extends CraftBlockData implements CommandBlock, Directional {
   private static final BooleanProperty CONDITIONAL = getBoolean(net.minecraft.world.level.block.CommandBlock.class, "conditional");
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(net.minecraft.world.level.block.CommandBlock.class, "facing", BlockFace.class);

   public CraftCommand() {
   }

   public CraftCommand(BlockState state) {
      super(state);
   }

   public boolean isConditional() {
      return (Boolean)this.get(CONDITIONAL);
   }

   public void setConditional(boolean conditional) {
      this.set(CONDITIONAL, conditional);
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
