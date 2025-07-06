package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Hopper;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftHopper extends CraftBlockData implements Hopper, Directional {
   private static final BooleanProperty ENABLED = getBoolean(HopperBlock.class, "enabled");
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(HopperBlock.class, "facing", BlockFace.class);

   public CraftHopper() {
   }

   public CraftHopper(BlockState state) {
      super(state);
   }

   public boolean isEnabled() {
      return (Boolean)this.get(ENABLED);
   }

   public void setEnabled(boolean enabled) {
      this.set(ENABLED, enabled);
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
