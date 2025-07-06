package org.bukkit.craftbukkit.v1_21_R5.entity;

import net.minecraft.world.entity.animal.AbstractFish;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Fish;

public class CraftFish extends CraftWaterMob implements Fish {
   public CraftFish(CraftServer server, AbstractFish entity) {
      super(server, entity);
   }

   public AbstractFish getHandle() {
      return (AbstractFish)this.entity;
   }

   public String toString() {
      return "CraftFish";
   }
}
