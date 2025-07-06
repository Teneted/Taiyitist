package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.projectile.AbstractThrownPotion;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.SplashPotion;

public class CraftSplashPotion extends CraftThrownPotion implements SplashPotion {
   public CraftSplashPotion(CraftServer server, AbstractThrownPotion entity) {
      super(server, entity);
   }
}
