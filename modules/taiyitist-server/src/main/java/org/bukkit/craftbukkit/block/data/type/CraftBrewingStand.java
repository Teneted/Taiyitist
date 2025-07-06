package org.bukkit.craftbukkit.block.data.type;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.BrewingStand;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftBrewingStand extends CraftBlockData implements BrewingStand {
   private static final BooleanProperty[] HAS_BOTTLE = new BooleanProperty[]{getBoolean("has_bottle_0"), getBoolean("has_bottle_1"), getBoolean("has_bottle_2")};

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
