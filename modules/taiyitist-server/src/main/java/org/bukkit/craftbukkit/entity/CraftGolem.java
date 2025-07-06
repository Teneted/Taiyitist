package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.animal.AbstractGolem;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Golem;

public class CraftGolem extends CraftCreature implements Golem {
   public CraftGolem(CraftServer server, AbstractGolem entity) {
      super(server, entity);
   }

   public AbstractGolem getHandle() {
      return (AbstractGolem)this.entity;
   }

   public String toString() {
      return "CraftGolem";
   }
}
