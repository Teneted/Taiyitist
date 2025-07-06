package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Illusioner;

public class CraftIllusioner extends CraftSpellcaster implements Illusioner {
   public CraftIllusioner(CraftServer server, net.minecraft.world.entity.monster.Illusioner entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.monster.Illusioner getHandle() {
      return (net.minecraft.world.entity.monster.Illusioner)super.getHandle();
   }

   public String toString() {
      return "CraftIllusioner";
   }
}
