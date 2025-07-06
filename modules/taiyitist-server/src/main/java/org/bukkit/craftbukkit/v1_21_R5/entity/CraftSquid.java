package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Squid;

public class CraftSquid extends CraftAgeable implements Squid {
   public CraftSquid(CraftServer server, net.minecraft.world.entity.animal.Squid entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.Squid getHandle() {
      return (net.minecraft.world.entity.animal.Squid)this.entity;
   }

   public String toString() {
      return "CraftSquid";
   }
}
