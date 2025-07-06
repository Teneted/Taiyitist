package org.bukkit.craftbukkit.v1_21_R5.damage;

import net.minecraft.world.damagesource.DamageEffects;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_21_R5.CraftSound;
import org.bukkit.damage.DamageEffect;

public class CraftDamageEffect implements DamageEffect {
   private final DamageEffects damageEffects;

   public CraftDamageEffect(DamageEffects damageEffects) {
      this.damageEffects = damageEffects;
   }

   public DamageEffects getHandle() {
      return this.damageEffects;
   }

   public Sound getSound() {
      return CraftSound.minecraftToBukkit(this.getHandle().sound());
   }

   public static DamageEffect getById(String id) {
      DamageEffects[] var1 = DamageEffects.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         DamageEffects damageEffects = var1[var3];
         if (damageEffects.getSerializedName().equalsIgnoreCase(id)) {
            return toBukkit(damageEffects);
         }
      }

      return null;
   }

   public static DamageEffect toBukkit(DamageEffects damageEffects) {
      return new CraftDamageEffect(damageEffects);
   }
}
