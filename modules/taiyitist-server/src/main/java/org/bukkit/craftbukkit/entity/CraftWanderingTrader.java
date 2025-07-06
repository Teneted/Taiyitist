package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.WanderingTrader;

public class CraftWanderingTrader extends CraftAbstractVillager implements WanderingTrader {
   public CraftWanderingTrader(CraftServer server, net.minecraft.world.entity.npc.WanderingTrader entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.npc.WanderingTrader getHandle() {
      return (net.minecraft.world.entity.npc.WanderingTrader)this.entity;
   }

   public String toString() {
      return "CraftWanderingTrader";
   }

   public int getDespawnDelay() {
      return this.getHandle().getDespawnDelay();
   }

   public void setDespawnDelay(int despawnDelay) {
      this.getHandle().setDespawnDelay(despawnDelay);
   }
}
