package org.bukkit.craftbukkit.entity;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Interaction;

public class CraftInteraction extends CraftEntity implements Interaction {
   public CraftInteraction(CraftServer server, net.minecraft.world.entity.Interaction entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.Interaction getHandle() {
      return (net.minecraft.world.entity.Interaction)super.getHandle();
   }

   public String toString() {
      return "CraftInteraction";
   }

   public float getInteractionWidth() {
      return this.getHandle().getWidth();
   }

   public void setInteractionWidth(float width) {
      this.getHandle().setWidth(width);
   }

   public float getInteractionHeight() {
      return this.getHandle().getHeight();
   }

   public void setInteractionHeight(float height) {
      this.getHandle().setHeight(height);
   }

   public boolean isResponsive() {
      return this.getHandle().getResponse();
   }

   public void setResponsive(boolean response) {
      this.getHandle().setResponse(response);
   }

   public Interaction.PreviousInteraction getLastAttack() {
      net.minecraft.world.entity.Interaction.PlayerAction last = this.getHandle().attack;
      return last != null ? new CraftPreviousInteraction(last.player(), last.timestamp()) : null;
   }

   public Interaction.PreviousInteraction getLastInteraction() {
      net.minecraft.world.entity.Interaction.PlayerAction last = this.getHandle().interaction;
      return last != null ? new CraftPreviousInteraction(last.player(), last.timestamp()) : null;
   }

   private static class CraftPreviousInteraction implements Interaction.PreviousInteraction {
      private final UUID uuid;
      private final long timestamp;

      public CraftPreviousInteraction(UUID uuid, long timestamp) {
         this.uuid = uuid;
         this.timestamp = timestamp;
      }

      public OfflinePlayer getPlayer() {
         return Bukkit.getOfflinePlayer(this.uuid);
      }

      public long getTimestamp() {
         return this.timestamp;
      }
   }
}
