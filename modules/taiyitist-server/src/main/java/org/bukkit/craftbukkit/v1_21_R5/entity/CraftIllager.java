package org.bukkit.craftbukkit.v1_21_R5.entity;

import net.minecraft.world.entity.monster.AbstractIllager;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Illager;

public class CraftIllager extends CraftRaider implements Illager {
   public CraftIllager(CraftServer server, AbstractIllager entity) {
      super(server, entity);
   }

   public AbstractIllager getHandle() {
      return (AbstractIllager)super.getHandle();
   }

   public String toString() {
      return "CraftIllager";
   }
}
