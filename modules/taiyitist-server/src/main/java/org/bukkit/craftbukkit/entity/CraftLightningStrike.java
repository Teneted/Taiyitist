package org.bukkit.craftbukkit.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LightningBolt;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;

public class CraftLightningStrike extends CraftEntity implements LightningStrike {
   private final LightningStrike.Spigot spigot = new LightningStrike.Spigot(this) {
      public boolean isSilent() {
         return false;
      }
   };

   public CraftLightningStrike(CraftServer server, LightningBolt entity) {
      super(server, entity);
   }

   public boolean isEffect() {
      return this.getHandle().visualOnly;
   }

   public int getFlashes() {
      return this.getHandle().flashes;
   }

   public void setFlashes(int flashes) {
      this.getHandle().flashes = flashes;
   }

   public int getLifeTicks() {
      return this.getHandle().life;
   }

   public void setLifeTicks(int ticks) {
      this.getHandle().life = ticks;
   }

   public Player getCausingPlayer() {
      ServerPlayer player = this.getHandle().getCause();
      return player != null ? player.getBukkitEntity() : null;
   }

   public void setCausingPlayer(Player player) {
      this.getHandle().setCause(player != null ? ((CraftPlayer)player).getHandle() : null);
   }

   public LightningBolt getHandle() {
      return (LightningBolt)this.entity;
   }

   public String toString() {
      return "CraftLightningStrike";
   }

   public LightningStrike.Spigot spigot() {
      return this.spigot;
   }
}
