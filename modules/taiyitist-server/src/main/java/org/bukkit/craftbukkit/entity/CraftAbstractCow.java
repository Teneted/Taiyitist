package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.animal.AbstractCow;
import org.bukkit.craftbukkit.CraftServer;

public abstract class CraftAbstractCow extends CraftAnimals {
   public CraftAbstractCow(CraftServer server, AbstractCow entity) {
      super(server, entity);
   }
}
