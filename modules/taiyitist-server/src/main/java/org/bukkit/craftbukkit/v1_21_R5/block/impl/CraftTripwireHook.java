package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.TripWireHookBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Attachable;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.TripwireHook;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftTripwireHook extends CraftBlockData implements TripwireHook, Attachable, Directional, Powerable {
   private static final BooleanProperty ATTACHED = getBoolean(TripWireHookBlock.class, "attached");
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(TripWireHookBlock.class, "facing", BlockFace.class);
   private static final BooleanProperty POWERED = getBoolean(TripWireHookBlock.class, "powered");

   public CraftTripwireHook() {
   }

   public CraftTripwireHook(BlockState state) {
      super(state);
   }

   public boolean isAttached() {
      return (Boolean)this.get(ATTACHED);
   }

   public void setAttached(boolean attached) {
      this.set(ATTACHED, attached);
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
