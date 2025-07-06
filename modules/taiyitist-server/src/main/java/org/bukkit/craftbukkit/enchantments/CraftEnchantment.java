package org.bukkit.craftbukkit.enchantments;

import com.google.common.base.Preconditions;
import java.util.Locale;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.legacy.FieldRename;
import org.bukkit.craftbukkit.util.ApiVersion;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftEnchantment extends Enchantment implements Handleable<net.minecraft.world.item.enchantment.Enchantment> {
   private final NamespacedKey key;
   private final Holder<net.minecraft.world.item.enchantment.Enchantment> handle;

   public static Enchantment minecraftToBukkit(net.minecraft.world.item.enchantment.Enchantment minecraft) {
      return (Enchantment)CraftRegistry.minecraftToBukkit(minecraft, Registries.ENCHANTMENT, Registry.ENCHANTMENT);
   }

   public static Enchantment minecraftHolderToBukkit(Holder<net.minecraft.world.item.enchantment.Enchantment> minecraft) {
      return minecraftToBukkit((net.minecraft.world.item.enchantment.Enchantment)minecraft.value());
   }

   public static net.minecraft.world.item.enchantment.Enchantment bukkitToMinecraft(Enchantment bukkit) {
      return (net.minecraft.world.item.enchantment.Enchantment)CraftRegistry.bukkitToMinecraft(bukkit);
   }

   public static Holder<net.minecraft.world.item.enchantment.Enchantment> bukkitToMinecraftHolder(Enchantment bukkit) {
      return CraftRegistry.bukkitToMinecraftHolder(bukkit, Registries.ENCHANTMENT);
   }

   public static String bukkitToString(Enchantment bukkit) {
      Preconditions.checkArgument(bukkit != null);
      return bukkit.getKey().toString();
   }

   public static Enchantment stringToBukkit(String string) {
      Preconditions.checkArgument(string != null);
      string = FieldRename.convertEnchantmentName(ApiVersion.CURRENT, string);
      string = string.toLowerCase(Locale.ROOT);
      NamespacedKey key = NamespacedKey.fromString(string);
      return (Enchantment)CraftRegistry.get(Registry.ENCHANTMENT, key, ApiVersion.CURRENT);
   }

   public CraftEnchantment(NamespacedKey key, net.minecraft.world.item.enchantment.Enchantment handle) {
      this.key = key;
      this.handle = CraftRegistry.getMinecraftRegistry(Registries.ENCHANTMENT).wrapAsHolder(handle);
   }

   public net.minecraft.world.item.enchantment.Enchantment getHandle() {
      return (net.minecraft.world.item.enchantment.Enchantment)this.handle.value();
   }

   public NamespacedKey getKey() {
      return this.getKeyOrThrow();
   }

   public int getMaxLevel() {
      return this.getHandle().getMaxLevel();
   }

   public int getStartLevel() {
      return this.getHandle().getMinLevel();
   }

   public EnchantmentTarget getItemTarget() {
      throw new UnsupportedOperationException("Method no longer applicable. Use Tags instead.");
   }

   public boolean isTreasure() {
      return !this.handle.is(EnchantmentTags.IN_ENCHANTING_TABLE);
   }

   public boolean isCursed() {
      return this.handle.is(EnchantmentTags.CURSE);
   }

   public boolean canEnchantItem(ItemStack item) {
      return this.getHandle().canEnchant(CraftItemStack.asNMSCopy(item));
   }

   public String getName() {
      if (!this.getKey().getNamespace().equals("minecraft")) {
         return this.getKey().toString();
      } else {
         String var10000;
         switch (this.getKey().getKey().toUpperCase(Locale.ROOT)) {
            case "PROTECTION" -> var10000 = "PROTECTION_ENVIRONMENTAL";
            case "FIRE_PROTECTION" -> var10000 = "PROTECTION_FIRE";
            case "FEATHER_FALLING" -> var10000 = "PROTECTION_FALL";
            case "BLAST_PROTECTION" -> var10000 = "PROTECTION_EXPLOSIONS";
            case "PROJECTILE_PROTECTION" -> var10000 = "PROTECTION_PROJECTILE";
            case "RESPIRATION" -> var10000 = "OXYGEN";
            case "AQUA_AFFINITY" -> var10000 = "WATER_WORKER";
            case "SHARPNESS" -> var10000 = "DAMAGE_ALL";
            case "SMITE" -> var10000 = "DAMAGE_UNDEAD";
            case "BANE_OF_ARTHROPODS" -> var10000 = "DAMAGE_ARTHROPODS";
            case "LOOTING" -> var10000 = "LOOT_BONUS_MOBS";
            case "EFFICIENCY" -> var10000 = "DIG_SPEED";
            case "UNBREAKING" -> var10000 = "DURABILITY";
            case "FORTUNE" -> var10000 = "LOOT_BONUS_BLOCKS";
            case "POWER" -> var10000 = "ARROW_DAMAGE";
            case "PUNCH" -> var10000 = "ARROW_KNOCKBACK";
            case "FLAME" -> var10000 = "ARROW_FIRE";
            case "INFINITY" -> var10000 = "ARROW_INFINITE";
            case "LUCK_OF_THE_SEA" -> var10000 = "LUCK";
            default -> var10000 = keyName;
         }

         return var10000;
      }
   }

   public boolean conflictsWith(Enchantment other) {
      if (other instanceof EnchantmentWrapper) {
         other = ((EnchantmentWrapper)other).getEnchantment();
      }

      if (!(other instanceof CraftEnchantment ench)) {
         return false;
      } else {
         return !net.minecraft.world.item.enchantment.Enchantment.areCompatible(this.handle, ench.handle);
      }
   }

   public String getTranslationKey() {
      return Util.makeDescriptionId("enchantment", ((ResourceKey)this.handle.unwrapKey().get()).location());
   }

   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else {
         return !(other instanceof CraftEnchantment) ? false : this.getKey().equals(((Enchantment)other).getKey());
      }
   }

   public int hashCode() {
      return this.getKey().hashCode();
   }

   public String toString() {
      return "CraftEnchantment[" + String.valueOf(this.getKey()) + "]";
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
