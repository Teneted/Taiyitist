package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.ChestedHorse;

public abstract class CraftChestedHorse extends CraftAbstractHorse implements ChestedHorse {
   public CraftChestedHorse(CraftServer server, AbstractChestedHorse entity) {
      super(server, entity);
   }

   public AbstractChestedHorse getHandle() {
      return (AbstractChestedHorse)super.getHandle();
   }

   public boolean isCarryingChest() {
      return this.getHandle().hasChest();
   }

   public void setCarryingChest(boolean chest) {
      if (chest != this.isCarryingChest()) {
         this.getHandle().setChest(chest);
         this.getHandle().createInventory();
      }
   }
}
