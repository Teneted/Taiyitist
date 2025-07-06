package org.bukkit.craftbukkit.v1_21_R5.potion;

import com.google.common.base.Preconditions;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.v1_21_R5.CraftRegistry;
import org.bukkit.craftbukkit.v1_21_R5.util.Handleable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftPotionEffectType extends PotionEffectType implements Handleable<MobEffect> {
   private final NamespacedKey key;
   private final MobEffect handle;
   private final int id;

   public static PotionEffectType minecraftHolderToBukkit(Holder<MobEffect> minecraft) {
      return minecraftToBukkit((MobEffect)minecraft.value());
   }

   public static PotionEffectType minecraftToBukkit(MobEffect minecraft) {
      return (PotionEffectType)CraftRegistry.minecraftToBukkit(minecraft, Registries.MOB_EFFECT, Registry.EFFECT);
   }

   public static MobEffect bukkitToMinecraft(PotionEffectType bukkit) {
      return (MobEffect)CraftRegistry.bukkitToMinecraft(bukkit);
   }

   public static Holder<MobEffect> bukkitToMinecraftHolder(PotionEffectType bukkit) {
      return CraftRegistry.bukkitToMinecraftHolder(bukkit, Registries.MOB_EFFECT);
   }

   public CraftPotionEffectType(NamespacedKey key, MobEffect handle) {
      this.key = key;
      this.handle = handle;
      this.id = CraftRegistry.getMinecraftRegistry(Registries.MOB_EFFECT).getId(handle) + 1;
   }

   public MobEffect getHandle() {
      return this.handle;
   }

   @NotNull
   public NamespacedKey getKey() {
      return this.getKeyOrThrow();
   }

   public double getDurationModifier() {
      return 1.0;
   }

   public int getId() {
      return this.id;
   }

   public String getName() {
      String var10000;
      switch (this.getId()) {
         case 1 -> var10000 = "SPEED";
         case 2 -> var10000 = "SLOW";
         case 3 -> var10000 = "FAST_DIGGING";
         case 4 -> var10000 = "SLOW_DIGGING";
         case 5 -> var10000 = "INCREASE_DAMAGE";
         case 6 -> var10000 = "HEAL";
         case 7 -> var10000 = "HARM";
         case 8 -> var10000 = "JUMP";
         case 9 -> var10000 = "CONFUSION";
         case 10 -> var10000 = "REGENERATION";
         case 11 -> var10000 = "DAMAGE_RESISTANCE";
         case 12 -> var10000 = "FIRE_RESISTANCE";
         case 13 -> var10000 = "WATER_BREATHING";
         case 14 -> var10000 = "INVISIBILITY";
         case 15 -> var10000 = "BLINDNESS";
         case 16 -> var10000 = "NIGHT_VISION";
         case 17 -> var10000 = "HUNGER";
         case 18 -> var10000 = "WEAKNESS";
         case 19 -> var10000 = "POISON";
         case 20 -> var10000 = "WITHER";
         case 21 -> var10000 = "HEALTH_BOOST";
         case 22 -> var10000 = "ABSORPTION";
         case 23 -> var10000 = "SATURATION";
         case 24 -> var10000 = "GLOWING";
         case 25 -> var10000 = "LEVITATION";
         case 26 -> var10000 = "LUCK";
         case 27 -> var10000 = "UNLUCK";
         case 28 -> var10000 = "SLOW_FALLING";
         case 29 -> var10000 = "CONDUIT_POWER";
         case 30 -> var10000 = "DOLPHINS_GRACE";
         case 31 -> var10000 = "BAD_OMEN";
         case 32 -> var10000 = "HERO_OF_THE_VILLAGE";
         case 33 -> var10000 = "DARKNESS";
         default -> var10000 = this.getKey().toString();
      }

      return var10000;
   }

   @NotNull
   public PotionEffect createEffect(int duration, int amplifier) {
      return new PotionEffect(this, this.isInstant() ? 1 : (int)((double)duration * this.getDurationModifier()), amplifier);
   }

   public boolean isInstant() {
      return this.handle.isInstantenous();
   }

   public PotionEffectTypeCategory getCategory() {
      return CraftPotionEffectTypeCategory.minecraftToBukkit(this.handle.getCategory());
   }

   public Color getColor() {
      return Color.fromRGB(this.handle.getColor());
   }

   @NotNull
   public String getTranslationKey() {
      return this.handle.getDescriptionId();
   }

   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else {
         return !(other instanceof PotionEffectType) ? false : this.getKey().equals(((PotionEffectType)other).getKey());
      }
   }

   public int hashCode() {
      return this.getKey().hashCode();
   }

   public String toString() {
      return "CraftPotionEffectType[" + String.valueOf(this.getKey()) + "]";
   }

   @NotNull
   public NamespacedKey getKeyOrThrow() {
      Preconditions.checkState(this.isRegistered(), "Cannot get key of this registry item, because it is not registered. Use #isRegistered() before calling this method.");
      return this.key;
   }

   @Nullable
   public NamespacedKey getKeyOrNull() {
      return this.key;
   }

   public boolean isRegistered() {
      return this.key != null;
   }
}
