package org.bukkit.craftbukkit.v1_21_R5.entity;

import net.minecraft.world.entity.ambient.AmbientCreature;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Ambient;

public class CraftAmbient extends CraftMob implements Ambient {
   public CraftAmbient(CraftServer server, AmbientCreature entity) {
      super(server, entity);
   }

   public AmbientCreature getHandle() {
      return (AmbientCreature)this.entity;
   }

   public String toString() {
      return "CraftAmbient";
   }
}
