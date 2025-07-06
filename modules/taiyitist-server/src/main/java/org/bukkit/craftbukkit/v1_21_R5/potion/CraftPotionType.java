package org.bukkit.craftbukkit.v1_21_R5.potion;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.alchemy.Potion;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R5.CraftRegistry;
import org.bukkit.craftbukkit.v1_21_R5.legacy.FieldRename;
import org.bukkit.craftbukkit.v1_21_R5.util.ApiVersion;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftNamespacedKey;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class CraftPotionType implements PotionType.InternalPotionData {
   private final NamespacedKey key;
   private final Potion potion;
   private final Supplier<List<PotionEffect>> potionEffects;
   private final Supplier<Boolean> upgradeable;
   private final Supplier<Boolean> extendable;
   private final Supplier<Integer> maxLevel;

   public static PotionType minecraftHolderToBukkit(Holder<Potion> minecraft) {
      return minecraftToBukkit((Potion)minecraft.value());
   }

   public static PotionType minecraftToBukkit(Potion minecraft) {
      Preconditions.checkArgument(minecraft != null);
      Registry<Potion> registry = CraftRegistry.getMinecraftRegistry(Registries.POTION);
      PotionType bukkit = (PotionType)org.bukkit.Registry.POTION.get(CraftNamespacedKey.fromMinecraft(((ResourceKey)registry.getResourceKey(minecraft).orElseThrow()).location()));
      Preconditions.checkArgument(bukkit != null);
      return bukkit;
   }

   public static Potion bukkitToMinecraft(PotionType bukkit) {
      Preconditions.checkArgument(bukkit != null);
      return (Potion)CraftRegistry.getMinecraftRegistry(Registries.POTION).getOptional(CraftNamespacedKey.toMinecraft(bukkit.getKey())).orElseThrow();
   }

   public static Holder<Potion> bukkitToMinecraftHolder(PotionType bukkit) {
      Preconditions.checkArgument(bukkit != null);
      Registry<Potion> registry = CraftRegistry.getMinecraftRegistry(Registries.POTION);
      Holder var3 = registry.wrapAsHolder(bukkitToMinecraft(bukkit));
      if (var3 instanceof Holder.Reference<Potion> holder) {
         return holder;
      } else {
         throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkit) + ", this can happen if a plugin creates its own sound effect with out properly registering it.");
      }
   }

   public static String bukkitToString(PotionType bukkit) {
      Preconditions.checkArgument(bukkit != null);
      return bukkit.getKey().toString();
   }

   public static PotionType stringToBukkit(String string) {
      Preconditions.checkArgument(string != null);
      string = FieldRename.convertPotionTypeName(ApiVersion.CURRENT, string);
      string = string.toLowerCase(Locale.ROOT);
      NamespacedKey key = NamespacedKey.fromString(string);
      return (PotionType)CraftRegistry.get(org.bukkit.Registry.POTION, key, ApiVersion.CURRENT);
   }

   public CraftPotionType(NamespacedKey key, Potion potion) {
      this.key = key;
      this.potion = potion;
      this.potionEffects = Suppliers.memoize(() -> {
         return potion.getEffects().stream().map(CraftPotionUtil::toBukkit).toList();
      });
      this.upgradeable = Suppliers.memoize(() -> {
         org.bukkit.Registry var10000 = org.bukkit.Registry.POTION;
         String var10003 = key.getNamespace();
         String var10004 = key.getKey();
         return var10000.get(new NamespacedKey(var10003, "strong_" + var10004)) != null;
      });
      this.extendable = Suppliers.memoize(() -> {
         org.bukkit.Registry var10000 = org.bukkit.Registry.POTION;
         String var10003 = key.getNamespace();
         String var10004 = key.getKey();
         return var10000.get(new NamespacedKey(var10003, "long_" + var10004)) != null;
      });
      this.maxLevel = Suppliers.memoize(() -> {
         return this.isUpgradeable() ? 2 : 1;
      });
   }

   public PotionEffectType getEffectType() {
      return this.getPotionEffects().isEmpty() ? null : ((PotionEffect)this.getPotionEffects().get(0)).getType();
   }

   public List<PotionEffect> getPotionEffects() {
      return (List)this.potionEffects.get();
   }

   public boolean isInstant() {
      return this.potion.hasInstantEffects();
   }

   public boolean isUpgradeable() {
      return (Boolean)this.upgradeable.get();
   }

   public boolean isExtendable() {
      return (Boolean)this.extendable.get();
   }

   public int getMaxLevel() {
      return (Integer)this.maxLevel.get();
   }
}
