package org.bukkit.craftbukkit.v1_21_R5.entity;

import java.util.UUID;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityTargetEvent;

public class CraftTameableAnimal extends CraftAnimals implements Tameable, Creature {
   public CraftTameableAnimal(CraftServer server, TamableAnimal entity) {
      super(server, entity);
   }

   public TamableAnimal getHandle() {
      return (TamableAnimal)super.getHandle();
   }

   public UUID getOwnerUUID() {
      EntityReference<LivingEntity> owner = this.getHandle().getOwnerReference();
      return owner != null ? owner.getUUID() : null;
   }

   public void setOwnerUUID(UUID uuid) {
      this.getHandle().setOwnerReference(uuid != null ? new EntityReference(uuid) : null);
   }

   public AnimalTamer getOwner() {
      if (this.getOwnerUUID() == null) {
         return null;
      } else {
         AnimalTamer owner = this.getServer().getPlayer(this.getOwnerUUID());
         if (owner == null) {
            owner = this.getServer().getOfflinePlayer(this.getOwnerUUID());
         }

         return (AnimalTamer)owner;
      }
   }

   public boolean isTamed() {
      return this.getHandle().isTame();
   }

   public void setOwner(AnimalTamer tamer) {
      if (tamer != null) {
         this.setTamed(true);
         this.getHandle().setTarget((LivingEntity)null, (EntityTargetEvent.TargetReason)null, false);
         this.setOwnerUUID(tamer.getUniqueId());
      } else {
         this.setTamed(false);
         this.setOwnerUUID((UUID)null);
      }

   }

   public void setTamed(boolean tame) {
      this.getHandle().setTame(tame, true);
      if (!tame) {
         this.setOwnerUUID((UUID)null);
      }

   }

   public boolean isSitting() {
      return this.getHandle().isInSittingPose();
   }

   public void setSitting(boolean sitting) {
      this.getHandle().setInSittingPose(sitting);
      this.getHandle().setOrderedToSit(sitting);
   }

   public String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{owner=" + String.valueOf(this.getOwner()) + ",tamed=" + this.isTamed() + "}";
   }
}
