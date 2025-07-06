package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;

public class CraftTNTPrimed extends CraftEntity implements TNTPrimed {
   public CraftTNTPrimed(CraftServer server, PrimedTnt entity) {
      super(server, entity);
   }

   public float getYield() {
      return this.getHandle().explosionPower;
   }

   public boolean isIncendiary() {
      return this.getHandle().bridge$isIncendiary();
   }

   public void setIsIncendiary(boolean isIncendiary) {
      this.getHandle().banner$setIsIncendiary(isIncendiary);
   }

   public void setYield(float yield) {
      this.getHandle().explosionPower = yield;
   }

   public int getFuseTicks() {
      return this.getHandle().getFuse();
   }

   public void setFuseTicks(int fuseTicks) {
      this.getHandle().setFuse(fuseTicks);
   }

   public PrimedTnt getHandle() {
      return (PrimedTnt)this.entity;
   }

   public String toString() {
      return "CraftTNTPrimed";
   }

   public Entity getSource() {
      LivingEntity source = this.getHandle().getOwner();
      return source != null ? source.getBukkitEntity() : null;
   }

   public void setSource(Entity source) {
      if (source instanceof org.bukkit.entity.LivingEntity) {
         this.getHandle().owner = new EntityReference(((CraftLivingEntity)source).getHandle());
      } else {
         this.getHandle().owner = null;
      }

   }
}
