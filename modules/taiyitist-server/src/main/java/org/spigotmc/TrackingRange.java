package org.spigotmc;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Ghast;

public class TrackingRange {
   public static int getEntityTrackingRange(Entity entity, int defaultRange) {
      if (defaultRange == 0) {
         return defaultRange;
      } else {
         SpigotWorldConfig config = entity.level().spigotConfig;
         if (entity instanceof ServerPlayer) {
            return config.playerTrackingRange;
         } else if (entity.activationType != ActivationRange.ActivationType.MONSTER && entity.activationType != ActivationRange.ActivationType.RAIDER) {
            if (entity instanceof Ghast) {
               return config.monsterTrackingRange > config.monsterActivationRange ? config.monsterTrackingRange : config.monsterActivationRange;
            } else if (entity.activationType == ActivationRange.ActivationType.ANIMAL) {
               return config.animalTrackingRange;
            } else if (!(entity instanceof ItemFrame) && !(entity instanceof Painting) && !(entity instanceof ItemEntity) && !(entity instanceof ExperienceOrb)) {
               return entity instanceof Display ? config.displayTrackingRange : config.otherTrackingRange;
            } else {
               return config.miscTrackingRange;
            }
         } else {
            return config.monsterTrackingRange;
         }
      }
   }
}
