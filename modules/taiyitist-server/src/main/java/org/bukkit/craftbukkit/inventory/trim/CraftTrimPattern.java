package org.bukkit.craftbukkit.inventory.trim;

import com.google.common.base.Preconditions;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.registry.CraftRegistryItem;
import org.jetbrains.annotations.NotNull;

public class CraftTrimPattern extends CraftRegistryItem<TrimPattern> implements org.bukkit.inventory.meta.trim.TrimPattern {
   public static org.bukkit.inventory.meta.trim.TrimPattern minecraftToBukkit(TrimPattern minecraft) {
      return (org.bukkit.inventory.meta.trim.TrimPattern)CraftRegistry.minecraftToBukkit(minecraft, Registries.TRIM_PATTERN, Registry.TRIM_PATTERN);
   }

   public static org.bukkit.inventory.meta.trim.TrimPattern minecraftHolderToBukkit(Holder<TrimPattern> minecraft) {
      return minecraftToBukkit((TrimPattern)minecraft.value());
   }

   public static TrimPattern bukkitToMinecraft(org.bukkit.inventory.meta.trim.TrimPattern bukkit) {
      return (TrimPattern)CraftRegistry.bukkitToMinecraft(bukkit);
   }

   public static Holder<TrimPattern> bukkitToMinecraftHolder(org.bukkit.inventory.meta.trim.TrimPattern bukkit) {
      Preconditions.checkArgument(bukkit != null);
      net.minecraft.core.Registry<TrimPattern> registry = CraftRegistry.getMinecraftRegistry(Registries.TRIM_PATTERN);
      Holder var3 = registry.wrapAsHolder(bukkitToMinecraft(bukkit));
      if (var3 instanceof Holder.Reference<TrimPattern> holder) {
         return holder;
      } else {
         throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkit) + ", this can happen if a plugin creates its own trim pattern without properly registering it.");
      }
   }

   public CraftTrimPattern(NamespacedKey key, Holder<TrimPattern> handle) {
      super(key, handle);
   }

   @NotNull
   public NamespacedKey getKey() {
      return this.getKeyOrThrow();
   }

   @NotNull
   public String getTranslationKey() {
      return ((TranslatableContents)((TrimPattern)this.getHandle()).description().getContents()).getKey();
   }
}
