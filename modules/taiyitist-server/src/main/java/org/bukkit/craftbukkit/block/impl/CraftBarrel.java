package org.bukkit.craftbukkit.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Barrel;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftBarrel extends CraftBlockData implements Barrel, Directional, Openable {
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(BarrelBlock.class, "facing", BlockFace.class);
   private static final BooleanProperty OPEN = getBoolean(BarrelBlock.class, "open");

   public CraftBarrel() {
   }

   public CraftBarrel(BlockState state) {
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

   public boolean isOpen() {
      return (Boolean)this.get(OPEN);
   }

   public void setOpen(boolean open) {
      this.set(OPEN, open);
   }
}
