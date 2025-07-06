package org.bukkit.craftbukkit.v1_21_R5.entity;

import net.minecraft.world.entity.animal.AbstractCow;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;

public abstract class CraftAbstractCow extends CraftAnimals {
   public CraftAbstractCow(CraftServer server, AbstractCow entity) {
      super(server, entity);
   }
}
