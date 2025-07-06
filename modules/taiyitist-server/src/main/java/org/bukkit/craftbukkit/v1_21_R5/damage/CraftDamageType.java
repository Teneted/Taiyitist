package org.bukkit.craftbukkit.v1_21_R5.damage;

import com.google.common.base.Preconditions;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageType;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R5.CraftRegistry;
import org.bukkit.craftbukkit.v1_21_R5.registry.CraftRegistryItem;
import org.bukkit.damage.DamageEffect;
import org.bukkit.damage.DamageScaling;
import org.bukkit.damage.DeathMessageType;

public class CraftDamageType extends CraftRegistryItem<DamageType> implements org.bukkit.damage.DamageType {
   public CraftDamageType(NamespacedKey key, Holder<DamageType> handle) {
      super(key, handle);
   }

   public String getTranslationKey() {
      return ((DamageType)this.getHandle()).msgId();
   }

   public DamageScaling getDamageScaling() {
      return damageScalingToBukkit(((DamageType)this.getHandle()).scaling());
   }

   public DamageEffect getDamageEffect() {
      return CraftDamageEffect.toBukkit(((DamageType)this.getHandle()).effects());
   }

   public DeathMessageType getDeathMessageType() {
      return deathMessageTypeToBukkit(((DamageType)this.getHandle()).deathMessageType());
   }

   public float getExhaustion() {
      return ((DamageType)this.getHandle()).exhaustion();
   }

   public NamespacedKey getKey() {
      return this.getKeyOrThrow();
   }

   public static DeathMessageType deathMessageTypeToBukkit(net.minecraft.world.damagesource.DeathMessageType deathMessageType) {
      DeathMessageType var10000;
      switch (deathMessageType) {
         case DEFAULT -> var10000 = DeathMessageType.DEFAULT;
         case FALL_VARIANTS -> var10000 = DeathMessageType.FALL_VARIANTS;
         case INTENTIONAL_GAME_DESIGN -> var10000 = DeathMessageType.INTENTIONAL_GAME_DESIGN;
         default -> throw new IllegalArgumentException("NMS DeathMessageType." + String.valueOf(deathMessageType) + " cannot be converted to a Bukkit DeathMessageType.");
      }

      return var10000;
   }

   public static net.minecraft.world.damagesource.DeathMessageType deathMessageTypeToNMS(DeathMessageType deathMessageType) {
      net.minecraft.world.damagesource.DeathMessageType var10000;
      switch (deathMessageType) {
         case DEFAULT -> var10000 = net.minecraft.world.damagesource.DeathMessageType.DEFAULT;
         case FALL_VARIANTS -> var10000 = net.minecraft.world.damagesource.DeathMessageType.FALL_VARIANTS;
         case INTENTIONAL_GAME_DESIGN -> var10000 = net.minecraft.world.damagesource.DeathMessageType.INTENTIONAL_GAME_DESIGN;
         default -> throw new IllegalArgumentException("Bukkit DeathMessageType." + String.valueOf(deathMessageType) + " cannot be converted to a NMS DeathMessageType.");
      }

      return var10000;
   }

   public static DamageScaling damageScalingToBukkit(net.minecraft.world.damagesource.DamageScaling damageScaling) {
      DamageScaling var10000;
      switch (damageScaling) {
         case ALWAYS -> var10000 = DamageScaling.ALWAYS;
         case WHEN_CAUSED_BY_LIVING_NON_PLAYER -> var10000 = DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER;
         case NEVER -> var10000 = DamageScaling.NEVER;
         default -> throw new IllegalArgumentException("NMS DamageScaling." + String.valueOf(damageScaling) + " cannot be converted to a Bukkit DamageScaling");
      }

      return var10000;
   }

   public static net.minecraft.world.damagesource.DamageScaling damageScalingToNMS(DamageScaling damageScaling) {
      net.minecraft.world.damagesource.DamageScaling var10000;
      switch (damageScaling) {
         case ALWAYS -> var10000 = net.minecraft.world.damagesource.DamageScaling.ALWAYS;
         case WHEN_CAUSED_BY_LIVING_NON_PLAYER -> var10000 = net.minecraft.world.damagesource.DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER;
         case NEVER -> var10000 = net.minecraft.world.damagesource.DamageScaling.NEVER;
         default -> throw new IllegalArgumentException("Bukkit DamageScaling." + String.valueOf(damageScaling) + " cannot be converted to a NMS DamageScaling");
      }

      return var10000;
   }

   public static org.bukkit.damage.DamageType minecraftHolderToBukkit(Holder<DamageType> minecraftHolder) {
      return minecraftToBukkit((DamageType)minecraftHolder.value());
   }

   public static Holder<DamageType> bukkitToMinecraftHolder(org.bukkit.damage.DamageType bukkitDamageType) {
      Preconditions.checkArgument(bukkitDamageType != null);
      Registry<DamageType> registry = CraftRegistry.getMinecraftRegistry(Registries.DAMAGE_TYPE);
      Holder var3 = registry.wrapAsHolder(bukkitToMinecraft(bukkitDamageType));
      if (var3 instanceof Holder.Reference<DamageType> holder) {
         return holder;
      } else {
         throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkitDamageType) + ", this can happen if a plugin creates its own damage type with out properly registering it.");
      }
   }

   public static DamageType bukkitToMinecraft(org.bukkit.damage.DamageType bukkitDamageType) {
      return (DamageType)CraftRegistry.bukkitToMinecraft(bukkitDamageType);
   }

   public static org.bukkit.damage.DamageType minecraftToBukkit(DamageType minecraftDamageType) {
      return (org.bukkit.damage.DamageType)CraftRegistry.minecraftToBukkit(minecraftDamageType, Registries.DAMAGE_TYPE, org.bukkit.Registry.DAMAGE_TYPE);
   }
}
