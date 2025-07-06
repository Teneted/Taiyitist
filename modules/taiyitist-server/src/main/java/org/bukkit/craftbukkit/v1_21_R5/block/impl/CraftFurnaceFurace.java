package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.type.Furnace;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftFurnaceFurace extends CraftBlockData implements Furnace, Directional, Lightable {
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(FurnaceBlock.class, "facing", BlockFace.class);
   private static final BooleanProperty LIT = getBoolean(FurnaceBlock.class, "lit");

   public CraftFurnaceFurace() {
   }

   public CraftFurnaceFurace(BlockState state) {
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
