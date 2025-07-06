package org.bukkit.craftbukkit.block.impl;

import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.Bamboo;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public final class CraftBamboo extends CraftBlockData implements Bamboo, Ageable, Sapling {
   private static final CraftBlockStateEnum<?, Bamboo.Leaves> LEAVES = getEnum(BambooStalkBlock.class, "leaves", Bamboo.Leaves.class);
   private static final IntegerProperty AGE = getInteger(BambooStalkBlock.class, "age");
   private static final IntegerProperty STAGE = getInteger(BambooStalkBlock.class, "stage");

   public CraftBamboo() {
   }

   public CraftBamboo(BlockState state) {
      super(state);
   }

   public Bamboo.Leaves getLeaves() {
      return (Bamboo.Leaves)this.get(LEAVES);
   }

   public void setLeaves(Bamboo.Leaves leaves) {
      this.set(LEAVES, leaves);
   }

   public int getAge() {
      return (Integer)this.get(AGE);
   }

   public void setAge(int age) {
      this.set(AGE, age);
   }

   public int getMaximumAge() {
      return getMax(AGE);
   }

   public int getStage() {
      return (Integer)this.get(STAGE);
   }

   public void setStage(int stage) {
      this.set(STAGE, stage);
   }

   public int getMaximumStage() {
      return getMax(STAGE);
   }
}
