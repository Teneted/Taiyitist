package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftDispenser extends CraftBlockData implements Dispenser, Directional {
   private static final BooleanProperty TRIGGERED = getBoolean(DispenserBlock.class, "triggered");
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(DispenserBlock.class, "facing", BlockFace.class);

   public CraftDispenser() {
   }

   public CraftDispenser(BlockState state) {
      super(state);
   }

   public boolean isTriggered() {
      return (Boolean)this.get(TRIGGERED);
   }

   public void setTriggered(boolean triggered) {
      this.set(TRIGGERED, triggered);
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
