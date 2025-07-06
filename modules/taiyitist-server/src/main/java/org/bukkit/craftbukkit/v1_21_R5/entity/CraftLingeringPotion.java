package org.bukkit.craftbukkit.v1_21_R5.entity;

import net.minecraft.world.entity.projectile.AbstractThrownPotion;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.LingeringPotion;

public class CraftLingeringPotion extends CraftThrownPotion implements LingeringPotion {
   public CraftLingeringPotion(CraftServer server, AbstractThrownPotion entity) {
      super(server, entity);
   }
}
