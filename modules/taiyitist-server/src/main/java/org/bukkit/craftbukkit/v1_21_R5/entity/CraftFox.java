package org.bukkit.craftbukkit.v1_21_R5.entity;

import com.google.common.base.Preconditions;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.animal.Fox.Variant;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Fox.Type;

public class CraftFox extends CraftAnimals implements Fox {
   public CraftFox(CraftServer server, net.minecraft.world.entity.animal.Fox entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.Fox getHandle() {
      return (net.minecraft.world.entity.animal.Fox)super.getHandle();
   }

   public String toString() {
      return "CraftFox";
   }

   public Fox.Type getFoxType() {
      return Type.values()[this.getHandle().getVariant().ordinal()];
   }

   public void setFoxType(Fox.Type type) {
      Preconditions.checkArgument(type != null, "type");
      this.getHandle().setVariant(Variant.values()[type.ordinal()]);
   }

   public boolean isCrouching() {
      return this.getHandle().isCrouching();
   }

   public void setCrouching(boolean crouching) {
      this.getHandle().setIsCrouching(crouching);
   }

   public boolean isSitting() {
      return this.getHandle().isSitting();
   }

   public void setSitting(boolean sitting) {
      this.getHandle().setSitting(sitting);
   }

   public void setSleeping(boolean sleeping) {
      this.getHandle().setSleeping(sleeping);
   }

   public AnimalTamer getFirstTrustedPlayer() {
      UUID uuid = (UUID)((Optional)this.getHandle().getEntityData().get(net.minecraft.world.entity.animal.Fox.DATA_TRUSTED_ID_0)).map(EntityReference::getUUID).orElse((Object)null);
      if (uuid == null) {
         return null;
      } else {
         AnimalTamer player = this.getServer().getPlayer(uuid);
         if (player == null) {
            player = this.getServer().getOfflinePlayer(uuid);
         }

         return (AnimalTamer)player;
      }
   }

   public void setFirstTrustedPlayer(AnimalTamer player) {
      if (player == null) {
         Preconditions.checkState(((Optional)this.getHandle().getEntityData().get(net.minecraft.world.entity.animal.Fox.DATA_TRUSTED_ID_1)).isEmpty(), "Must remove second trusted player first");
      }

      this.getHandle().getEntityData().set(net.minecraft.world.entity.animal.Fox.DATA_TRUSTED_ID_0, player == null ? Optional.empty() : Optional.of(new EntityReference(player.getUniqueId())));
   }

   public AnimalTamer getSecondTrustedPlayer() {
      UUID uuid = (UUID)((Optional)this.getHandle().getEntityData().get(net.minecraft.world.entity.animal.Fox.DATA_TRUSTED_ID_1)).map(EntityReference::getUUID).orElse((Object)null);
      if (uuid == null) {
         return null;
      } else {
         AnimalTamer player = this.getServer().getPlayer(uuid);
         if (player == null) {
            player = this.getServer().getOfflinePlayer(uuid);
         }

         return (AnimalTamer)player;
      }
   }

   public void setSecondTrustedPlayer(AnimalTamer player) {
      if (player != null) {
         Preconditions.checkState(((Optional)this.getHandle().getEntityData().get(net.minecraft.world.entity.animal.Fox.DATA_TRUSTED_ID_0)).isPresent(), "Must add first trusted player first");
      }

      this.getHandle().getEntityData().set(net.minecraft.world.entity.animal.Fox.DATA_TRUSTED_ID_1, player == null ? Optional.empty() : Optional.of(new EntityReference(player.getUniqueId())));
   }

   public boolean isFaceplanted() {
      return this.getHandle().isFaceplanted();
   }
}
