package org.bukkit.craftbukkit.v1_21_R5.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.CaveVinesPlant;
import org.bukkit.craftbukkit.v1_21_R5.block.data.CraftBlockData;

public abstract class CraftCaveVinesPlant extends CraftBlockData implements CaveVinesPlant {
   private static final BooleanProperty BERRIES = getBoolean("berries");

   public boolean isBerries() {
      return (Boolean)this.get(BERRIES);
   }

   public void setBerries(boolean berries) {
      this.set(BERRIES, berries);
   }
}
