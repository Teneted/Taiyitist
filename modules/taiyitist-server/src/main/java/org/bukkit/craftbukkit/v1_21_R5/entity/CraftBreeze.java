package org.bukkit.craftbukkit.v1_21_R5.entity;

import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Breeze;
import org.bukkit.entity.LivingEntity;

public class CraftBreeze extends CraftMonster implements Breeze {
   public CraftBreeze(CraftServer server, net.minecraft.world.entity.monster.breeze.Breeze entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.monster.breeze.Breeze getHandle() {
      return (net.minecraft.world.entity.monster.breeze.Breeze)this.entity;
   }

   public void setTarget(LivingEntity target) {
      super.setTarget(target);
      net.minecraft.world.entity.LivingEntity var10000;
      if (target instanceof CraftLivingEntity craftLivingEntity) {
         var10000 = craftLivingEntity.getHandle();
      } else {
         var10000 = null;
      }

      net.minecraft.world.entity.LivingEntity entityLivingTarget = var10000;
      this.getHandle().getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, entityLivingTarget);
   }

   public String toString() {
      return "CraftBreeze";
   }
}
