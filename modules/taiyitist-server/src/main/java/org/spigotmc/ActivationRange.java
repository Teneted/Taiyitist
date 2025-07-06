package org.spigotmc;

import java.util.Iterator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.bukkit.craftbukkit.SpigotTimings;

public class ActivationRange {
   static AABB maxBB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

   public static ActivationType initializeEntityActivationType(Entity entity) {
      if (entity instanceof Raider) {
         return ActivationRange.ActivationType.RAIDER;
      } else if (!(entity instanceof Monster) && !(entity instanceof Slime)) {
         return !(entity instanceof PathfinderMob) && !(entity instanceof AmbientCreature) ? ActivationRange.ActivationType.MISC : ActivationRange.ActivationType.ANIMAL;
      } else {
         return ActivationRange.ActivationType.MONSTER;
      }
   }

   public static boolean initializeEntityActivationState(Entity entity, SpigotWorldConfig config) {
      if (config == null) {
         return false;
      } else {
         return entity.activationType == ActivationRange.ActivationType.MISC && config.miscActivationRange == 0 || entity.activationType == ActivationRange.ActivationType.RAIDER && config.raiderActivationRange == 0 || entity.activationType == ActivationRange.ActivationType.ANIMAL && config.animalActivationRange == 0 || entity.activationType == ActivationRange.ActivationType.MONSTER && config.monsterActivationRange == 0 || entity instanceof Player || entity instanceof ThrowableProjectile || entity instanceof EnderDragon || entity instanceof EnderDragonPart || entity instanceof WitherBoss || entity instanceof AbstractHurtingProjectile || entity instanceof LightningBolt || entity instanceof PrimedTnt || entity instanceof EndCrystal || entity instanceof FireworkRocketEntity || entity instanceof ThrownTrident;
      }
   }

   public static void activateEntities(Level world) {
      SpigotTimings.entityActivationCheckTimer.startTiming();
      int miscActivationRange = world.spigotConfig.miscActivationRange;
      int raiderActivationRange = world.spigotConfig.raiderActivationRange;
      int animalActivationRange = world.spigotConfig.animalActivationRange;
      int monsterActivationRange = world.spigotConfig.monsterActivationRange;
      int maxRange = Math.max(monsterActivationRange, animalActivationRange);
      maxRange = Math.max(maxRange, raiderActivationRange);
      maxRange = Math.max(maxRange, miscActivationRange);
      maxRange = Math.min((world.spigotConfig.simulationDistance << 4) - 8, maxRange);
      Iterator var6 = world.players().iterator();

      while(true) {
         Player player;
         do {
            if (!var6.hasNext()) {
               SpigotTimings.entityActivationCheckTimer.stopTiming();
               return;
            }

            player = (Player)var6.next();
            player.activatedTick = (long)MinecraftServer.currentTick;
         } while(world.spigotConfig.ignoreSpectatorActivation && player.isSpectator());

         maxBB = player.getBoundingBox().inflate((double)maxRange, 256.0, (double)maxRange);
         ActivationRange.ActivationType.MISC.boundingBox = player.getBoundingBox().inflate((double)miscActivationRange, 256.0, (double)miscActivationRange);
         ActivationRange.ActivationType.RAIDER.boundingBox = player.getBoundingBox().inflate((double)raiderActivationRange, 256.0, (double)raiderActivationRange);
         ActivationRange.ActivationType.ANIMAL.boundingBox = player.getBoundingBox().inflate((double)animalActivationRange, 256.0, (double)animalActivationRange);
         ActivationRange.ActivationType.MONSTER.boundingBox = player.getBoundingBox().inflate((double)monsterActivationRange, 256.0, (double)monsterActivationRange);
         world.getEntities().get(maxBB, ActivationRange::activateEntity);
      }
   }

   private static void activateEntity(Entity entity) {
      if ((long)MinecraftServer.currentTick > entity.activatedTick) {
         if (entity.defaultActivationState) {
            entity.activatedTick = (long)MinecraftServer.currentTick;
            return;
         }

         if (entity.activationType.boundingBox.intersects(entity.getBoundingBox())) {
            entity.activatedTick = (long)MinecraftServer.currentTick;
         }
      }

   }

   public static boolean checkEntityImmunities(Entity entity) {
      if (!entity.wasTouchingWater && entity.getRemainingFireTicks() <= 0) {
         if (!(entity instanceof AbstractArrow)) {
            if (!entity.onGround() || !entity.passengers.isEmpty() || entity.isPassenger()) {
               return true;
            }
         } else if (!((AbstractArrow)entity).isInGround()) {
            return true;
         }

         if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;
            if (living.hurtTime > 0 || living.activeEffects.size() > 0) {
               return true;
            }

            if (entity instanceof PathfinderMob && ((PathfinderMob)entity).getTarget() != null) {
               return true;
            }

            if (entity instanceof Villager && ((Villager)entity).canBreed()) {
               return true;
            }

            if (entity instanceof Animal) {
               Animal animal = (Animal)entity;
               if (animal.isBaby() || animal.isInLove()) {
                  return true;
               }

               if (entity instanceof Sheep && ((Sheep)entity).isSheared()) {
                  return true;
               }
            }

            if (entity instanceof Creeper && ((Creeper)entity).isIgnited()) {
               return true;
            }
         }

         return entity instanceof ExperienceOrb;
      } else {
         return true;
      }
   }

   public static boolean checkIfActive(Entity entity) {
      SpigotTimings.checkIfActiveTimer.startTiming();
      if (entity instanceof FireworkRocketEntity || entity instanceof ItemEntity && (entity.tickCount + entity.getId() + 1) % 4 == 0) {
         SpigotTimings.checkIfActiveTimer.stopTiming();
         return true;
      } else {
         boolean isActive = entity.activatedTick >= (long)MinecraftServer.currentTick || entity.defaultActivationState;
         if (!isActive) {
            if (((long)MinecraftServer.currentTick - entity.activatedTick - 1L) % 20L == 0L) {
               if (checkEntityImmunities(entity)) {
                  entity.activatedTick = (long)(MinecraftServer.currentTick + 20);
               }

               isActive = true;
            }
         } else if (!entity.defaultActivationState && entity.tickCount % 4 == 0 && !checkEntityImmunities(entity)) {
            isActive = false;
         }

         SpigotTimings.checkIfActiveTimer.stopTiming();
         return isActive;
      }
   }

   public static enum ActivationType {
      MONSTER,
      ANIMAL,
      RAIDER,
      MISC;

      AABB boundingBox = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

      // $FF: synthetic method
      private static ActivationType[] $values() {
         return new ActivationType[]{MONSTER, ANIMAL, RAIDER, MISC};
      }
   }
}
