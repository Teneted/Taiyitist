package org.bukkit.craftbukkit.v1_21_R5.block.impl;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.BrewingStand;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public final class CraftBrewingStand extends CraftBlockData implements BrewingStand {
   private static final BooleanProperty[] HAS_BOTTLE = new BooleanProperty[]{getBoolean(BrewingStandBlock.class, "has_bottle_0"), getBoolean(BrewingStandBlock.class, "has_bottle_1"), getBoolean(BrewingStandBlock.class, "has_bottle_2")};

   public CraftBrewingStand() {
   }

   public CraftBrewingStand(BlockState state) {
      super(state);
   }

   public boolean hasBottle(int bottle) {
      return (Boolean)this.get(HAS_BOTTLE[bottle]);
   }

   public void setBottle(int bottle, boolean has) {
      this.set(HAS_BOTTLE[bottle], has);
   }

   public Set<Integer> getBottles() {
      ImmutableSet.Builder<Integer> bottles = ImmutableSet.builder();

      for(int index = 0; index < this.getMaximumBottles(); ++index) {
         if (this.hasBottle(index)) {
            bottles.add(index);
         }
      }

      return bottles.build();
   }

   public int getMaximumBottles() {
      return HAS_BOTTLE.length;
   }
}
