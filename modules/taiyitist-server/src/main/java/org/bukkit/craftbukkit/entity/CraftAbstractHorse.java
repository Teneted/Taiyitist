package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import java.util.UUID;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftInventoryAbstractHorse;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Horse;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.AbstractHorseInventory;

public abstract class CraftAbstractHorse extends CraftAnimals implements AbstractHorse {
   public CraftAbstractHorse(CraftServer server, net.minecraft.world.entity.animal.horse.AbstractHorse entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.horse.AbstractHorse getHandle() {
      return (net.minecraft.world.entity.animal.horse.AbstractHorse)this.entity;
   }

   public void setVariant(Horse.Variant variant) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public int getDomestication() {
      return this.getHandle().getTemper();
   }

   public void setDomestication(int value) {
      Preconditions.checkArgument(value >= 0 && value <= this.getMaxDomestication(), "Domestication level (%s) need to be between %s and %s (max domestication)", value, 0, this.getMaxDomestication());
      this.getHandle().setTemper(value);
   }

   public int getMaxDomestication() {
      return this.getHandle().getMaxTemper();
   }

   public void setMaxDomestication(int value) {
      Preconditions.checkArgument(value > 0, "Max domestication (%s) cannot be zero or less", value);
      this.getHandle().taiyitist$setMaxDomestication(value);
   }

   public double getJumpStrength() {
      return this.getHandle().getAttributeValue(Attributes.JUMP_STRENGTH);
   }

   public void setJumpStrength(double strength) {
      Preconditions.checkArgument(strength >= 0.0, "Jump strength (%s) cannot be less than zero", strength);
      this.getHandle().getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(strength);
   }

   public boolean isTamed() {
      return this.getHandle().isTamed();
   }

   public void setTamed(boolean tamed) {
      this.getHandle().setTamed(tamed);
   }

   public AnimalTamer getOwner() {
      return this.getOwnerUUID() == null ? null : this.getServer().getOfflinePlayer(this.getOwnerUUID());
   }

   public void setOwner(AnimalTamer owner) {
      if (owner != null) {
         this.setTamed(true);
         this.getHandle().setTarget((LivingEntity)null, (EntityTargetEvent.TargetReason)null, false);
         this.setOwnerUUID(owner.getUniqueId());
      } else {
         this.setTamed(false);
         this.setOwnerUUID((UUID)null);
      }

   }

   public UUID getOwnerUUID() {
      EntityReference<LivingEntity> owner = this.getHandle().getOwnerReference();
      return owner != null ? owner.getUUID() : null;
   }

   public void setOwnerUUID(UUID uuid) {
      this.getHandle().owner = uuid != null ? new EntityReference(uuid) : null;
   }

   public boolean isEatingHaystack() {
      return this.getHandle().isEating();
   }

   public void setEatingHaystack(boolean eatingHaystack) {
      this.getHandle().setEating(eatingHaystack);
   }

   public AbstractHorseInventory getInventory() {
      return new CraftInventoryAbstractHorse(this.getHandle().inventory, this.getHandle().equipment);
   }
}
