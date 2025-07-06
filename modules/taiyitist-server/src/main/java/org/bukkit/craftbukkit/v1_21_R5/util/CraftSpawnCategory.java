package org.bukkit.craftbukkit.v1_21_R5.util;

import net.minecraft.world.entity.MobCategory;
import org.bukkit.entity.SpawnCategory;

public class CraftSpawnCategory {
   public static boolean isValidForLimits(SpawnCategory spawnCategory) {
      return spawnCategory != null && spawnCategory != SpawnCategory.MISC;
   }

   public static String getConfigNameSpawnLimit(SpawnCategory spawnCategory) {
      String var10000;
      switch (spawnCategory) {
         case MONSTER -> var10000 = "spawn-limits.monsters";
         case ANIMAL -> var10000 = "spawn-limits.animals";
         case WATER_ANIMAL -> var10000 = "spawn-limits.water-animals";
         case WATER_AMBIENT -> var10000 = "spawn-limits.water-ambient";
         case WATER_UNDERGROUND_CREATURE -> var10000 = "spawn-limits.water-underground-creature";
         case AMBIENT -> var10000 = "spawn-limits.ambient";
         case AXOLOTL -> var10000 = "spawn-limits.axolotls";
         default -> throw new UnsupportedOperationException("Unknown Config value " + String.valueOf(spawnCategory) + " for spawn-limits");
      }

      return var10000;
   }

   public static String getConfigNameTicksPerSpawn(SpawnCategory spawnCategory) {
      String var10000;
      switch (spawnCategory) {
         case MONSTER -> var10000 = "ticks-per.monster-spawns";
         case ANIMAL -> var10000 = "ticks-per.animal-spawns";
         case WATER_ANIMAL -> var10000 = "ticks-per.water-spawns";
         case WATER_AMBIENT -> var10000 = "ticks-per.water-ambient-spawns";
         case WATER_UNDERGROUND_CREATURE -> var10000 = "ticks-per.water-underground-creature-spawns";
         case AMBIENT -> var10000 = "ticks-per.ambient-spawns";
         case AXOLOTL -> var10000 = "ticks-per.axolotl-spawns";
         default -> throw new UnsupportedOperationException("Unknown Config value " + String.valueOf(spawnCategory) + " for ticks-per");
      }

      return var10000;
   }

   public static long getDefaultTicksPerSpawn(SpawnCategory spawnCategory) {
      long var10000;
      switch (spawnCategory) {
         case MONSTER:
         case WATER_ANIMAL:
         case WATER_AMBIENT:
         case WATER_UNDERGROUND_CREATURE:
         case AMBIENT:
         case AXOLOTL:
            var10000 = 1L;
            break;
         case ANIMAL:
            var10000 = 400L;
            break;
         default:
            throw new UnsupportedOperationException("Unknown Config value " + String.valueOf(spawnCategory) + " for ticks-per");
      }

      return var10000;
   }

   public static SpawnCategory toBukkit(MobCategory enumCreatureType) {
      SpawnCategory var10000;
      switch (enumCreatureType) {
         case MONSTER -> var10000 = SpawnCategory.MONSTER;
         case CREATURE -> var10000 = SpawnCategory.ANIMAL;
         case AMBIENT -> var10000 = SpawnCategory.AMBIENT;
         case AXOLOTLS -> var10000 = SpawnCategory.AXOLOTL;
         case WATER_CREATURE -> var10000 = SpawnCategory.WATER_ANIMAL;
         case WATER_AMBIENT -> var10000 = SpawnCategory.WATER_AMBIENT;
         case UNDERGROUND_WATER_CREATURE -> var10000 = SpawnCategory.WATER_UNDERGROUND_CREATURE;
         case MISC -> var10000 = SpawnCategory.MISC;
         default -> throw new UnsupportedOperationException("Unknown EnumCreatureType " + String.valueOf(enumCreatureType) + " for SpawnCategory");
      }

      return var10000;
   }

   public static MobCategory toNMS(SpawnCategory spawnCategory) {
      MobCategory var10000;
      switch (spawnCategory) {
         case MONSTER -> var10000 = MobCategory.MONSTER;
         case ANIMAL -> var10000 = MobCategory.CREATURE;
         case WATER_ANIMAL -> var10000 = MobCategory.WATER_CREATURE;
         case WATER_AMBIENT -> var10000 = MobCategory.WATER_AMBIENT;
         case WATER_UNDERGROUND_CREATURE -> var10000 = MobCategory.UNDERGROUND_WATER_CREATURE;
         case AMBIENT -> var10000 = MobCategory.AMBIENT;
         case AXOLOTL -> var10000 = MobCategory.AXOLOTLS;
         case MISC -> var10000 = MobCategory.MISC;
         default -> throw new UnsupportedOperationException("Unknown SpawnCategory " + String.valueOf(spawnCategory) + " for EnumCreatureType");
      }

      return var10000;
   }
}
