package org.bukkit.craftbukkit.inventory.trim;

import com.google.common.base.Preconditions;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.registry.CraftRegistryItem;
import org.jetbrains.annotations.NotNull;

public class CraftTrimMaterial extends CraftRegistryItem<TrimMaterial> implements org.bukkit.inventory.meta.trim.TrimMaterial {
   public static org.bukkit.inventory.meta.trim.TrimMaterial minecraftToBukkit(TrimMaterial minecraft) {
      return (org.bukkit.inventory.meta.trim.TrimMaterial)CraftRegistry.minecraftToBukkit(minecraft, Registries.TRIM_MATERIAL, Registry.TRIM_MATERIAL);
   }

   public static org.bukkit.inventory.meta.trim.TrimMaterial minecraftHolderToBukkit(Holder<TrimMaterial> minecraft) {
      return minecraftToBukkit((TrimMaterial)minecraft.value());
   }

   public static TrimMaterial bukkitToMinecraft(org.bukkit.inventory.meta.trim.TrimMaterial bukkit) {
      return (TrimMaterial)CraftRegistry.bukkitToMinecraft(bukkit);
   }

   public static Holder<TrimMaterial> bukkitToMinecraftHolder(org.bukkit.inventory.meta.trim.TrimMaterial bukkit) {
      Preconditions.checkArgument(bukkit != null);
      net.minecraft.core.Registry<TrimMaterial> registry = CraftRegistry.getMinecraftRegistry(Registries.TRIM_MATERIAL);
      if (registry.wrapAsHolder(bukkitToMinecraft(bukkit)) instanceof Holder.Reference<TrimMaterial> holder) {
         return holder;
      } else {
         throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkit) + ", this can happen if a plugin creates its own trim material without properly registering it.");
      }
   }

   public CraftTrimMaterial(NamespacedKey key, Holder<TrimMaterial> handle) {
      super(key, handle);
   }

   @NotNull
   public NamespacedKey getKey() {
      return this.getKeyOrThrow();
   }

   @NotNull
   public String getTranslationKey() {
      return ((TranslatableContents)((TrimMaterial)this.getHandle()).description().getContents()).getKey();
   }
}
