package org.bukkit.craftbukkit.v1_21_R5.block.data;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.bukkit.block.data.Ageable;

public abstract class CraftAgeable extends CraftBlockData implements Ageable {
   private static final IntegerProperty AGE = getInteger("age");

   public int getAge() {
      return (Integer)this.get(AGE);
   }

   public void setAge(int age) {
      this.set(AGE, age);
   }

   public int getMaximumAge() {
      return getMax(AGE);
   }
}
