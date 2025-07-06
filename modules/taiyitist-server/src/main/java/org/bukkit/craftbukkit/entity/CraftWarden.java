package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.warden.WardenAi;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Warden;

public class CraftWarden extends CraftMonster implements Warden {
   public CraftWarden(CraftServer server, net.minecraft.world.entity.monster.warden.Warden entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.monster.warden.Warden getHandle() {
      return (net.minecraft.world.entity.monster.warden.Warden)this.entity;
   }

   public String toString() {
      return "CraftWarden";
   }

   public int getAnger() {
      return this.getHandle().getAngerManagement().getActiveAnger(this.getHandle().getTarget());
   }

   public int getAnger(Entity entity) {
      Preconditions.checkArgument(entity != null, "Entity cannot be null");
      return this.getHandle().getAngerManagement().getActiveAnger(((CraftEntity)entity).getHandle());
   }

   public void increaseAnger(Entity entity, int increase) {
      Preconditions.checkArgument(entity != null, "Entity cannot be null");
      this.getHandle().getAngerManagement().increaseAnger(((CraftEntity)entity).getHandle(), increase);
   }

   public void setAnger(Entity entity, int anger) {
      Preconditions.checkArgument(entity != null, "Entity cannot be null");
      this.getHandle().clearAnger(((CraftEntity)entity).getHandle());
      this.getHandle().getAngerManagement().increaseAnger(((CraftEntity)entity).getHandle(), anger);
   }

   public void clearAnger(Entity entity) {
      Preconditions.checkArgument(entity != null, "Entity cannot be null");
      this.getHandle().clearAnger(((CraftEntity)entity).getHandle());
   }

   public LivingEntity getEntityAngryAt() {
      return (LivingEntity)this.getHandle().getEntityAngryAt().map(net.minecraft.world.entity.Entity::getBukkitEntity).orElse((Object)null);
   }

   public void setDisturbanceLocation(Location location) {
      Preconditions.checkArgument(location != null, "Location cannot be null");
      WardenAi.setDisturbanceLocation(this.getHandle(), BlockPos.containing(location.getX(), location.getY(), location.getZ()));
   }

   public Warden.AngerLevel getAngerLevel() {
      Warden.AngerLevel var10000;
      switch (this.getHandle().getAngerLevel()) {
         case CALM -> var10000 = AngerLevel.CALM;
         case AGITATED -> var10000 = AngerLevel.AGITATED;
         case ANGRY -> var10000 = AngerLevel.ANGRY;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }
}
