package org.bukkit.craftbukkit.block.impl;

import net.minecraft.world.level.block.CaveVinesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.CaveVines;
import org.bukkit.block.data.type.CaveVinesPlant;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public final class CraftCaveVines extends CraftBlockData implements CaveVines, Ageable, CaveVinesPlant {
   private static final IntegerProperty AGE = getInteger(CaveVinesBlock.class, "age");
   private static final BooleanProperty BERRIES = getBoolean(CaveVinesBlock.class, "berries");

   public CraftCaveVines() {
   }

   public CraftCaveVines(BlockState state) {
      super(state);
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

   public boolean isBerries() {
      return (Boolean)this.get(BERRIES);
   }

   public void setBerries(boolean berries) {
      this.set(BERRIES, berries);
   }
}
