package org.bukkit.craftbukkit.v1_21_R5.entity;

import com.google.common.base.Preconditions;
import java.util.Optional;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_21_R5.CraftLootTable;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.CraftSound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.loot.LootTable;

public abstract class CraftMob extends CraftLivingEntity implements Mob {
   public CraftMob(CraftServer server, net.minecraft.world.entity.Mob entity) {
      super(server, entity);
   }

   public void setTarget(LivingEntity target) {
      Preconditions.checkState(!this.getHandle().generation, "Cannot set target during world generation");
      net.minecraft.world.entity.Mob entity = this.getHandle();
      if (target == null) {
         entity.setTarget((net.minecraft.world.entity.LivingEntity)null, (EntityTargetEvent.TargetReason)null, false);
      } else if (target instanceof CraftLivingEntity) {
         entity.setTarget(((CraftLivingEntity)target).getHandle(), (EntityTargetEvent.TargetReason)null, false);
      }

   }

   public CraftLivingEntity getTarget() {
      return this.getHandle().getTarget() == null ? null : (CraftLivingEntity)this.getHandle().getTarget().getBukkitEntity();
   }

   public void setAware(boolean aware) {
      this.getHandle().aware = aware;
   }

   public boolean isAware() {
      return this.getHandle().aware;
   }

   public Sound getAmbientSound() {
      SoundEvent sound = this.getHandle().getAmbientSound0();
      return sound != null ? CraftSound.minecraftToBukkit(sound) : null;
   }

   public net.minecraft.world.entity.Mob getHandle() {
      return (net.minecraft.world.entity.Mob)this.entity;
   }

   public String toString() {
      return "CraftMob";
   }

   public void setLootTable(LootTable table) {
      this.getHandle().lootTable = Optional.ofNullable(CraftLootTable.bukkitToMinecraft(table));
   }

   public LootTable getLootTable() {
      return CraftLootTable.minecraftToBukkit((ResourceKey)this.getHandle().getLootTable().orElse((Object)null));
   }

   public void setSeed(long seed) {
      this.getHandle().lootTableSeed = seed;
   }

   public long getSeed() {
      return this.getHandle().lootTableSeed;
   }
}
