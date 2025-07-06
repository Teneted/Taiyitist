package org.bukkit.craftbukkit.block.data.type;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.bukkit.block.data.type.Crafter;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockStateEnum;

public abstract class CraftCrafter extends CraftBlockData implements Crafter {
   private static final BooleanProperty CRAFTING = getBoolean("crafting");
   private static final BooleanProperty TRIGGERED = getBoolean("triggered");
   private static final CraftBlockStateEnum<?, Crafter.Orientation> ORIENTATION = getEnum("orientation", Crafter.Orientation.class);

   public boolean isCrafting() {
      return (Boolean)this.get(CRAFTING);
   }

   public void setCrafting(boolean crafting) {
      this.set(CRAFTING, crafting);
   }

   public boolean isTriggered() {
      return (Boolean)this.get(TRIGGERED);
   }

   public void setTriggered(boolean triggered) {
      this.set(TRIGGERED, triggered);
   }

   public Crafter.Orientation getOrientation() {
      return (Crafter.Orientation)this.get(ORIENTATION);
   }

   public void setOrientation(Crafter.Orientation orientation) {
      this.set(ORIENTATION, orientation);
   }
}
