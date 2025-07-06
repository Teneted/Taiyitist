package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import java.util.Set;
import net.minecraft.world.level.block.FlowerBedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.FlowerBed;
import org.bukkit.block.data.type.PinkPetals;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockStateEnum;

public final class CraftFlowerBed extends CraftBlockData implements PinkPetals, FlowerBed, Directional {
   private static final IntegerProperty FLOWER_AMOUNT = getInteger(FlowerBedBlock.class, "flower_amount");
   private static final CraftBlockStateEnum<?, BlockFace> FACING = getEnum(FlowerBedBlock.class, "facing", BlockFace.class);

   public CraftFlowerBed() {
   }

   public CraftFlowerBed(BlockState state) {
      super(state);
   }

   public int getFlowerAmount() {
      return (Integer)this.get(FLOWER_AMOUNT);
   }

   public void setFlowerAmount(int flower_amount) {
      this.set(FLOWER_AMOUNT, flower_amount);
   }

   public int getMaximumFlowerAmount() {
      return getMax(FLOWER_AMOUNT);
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
