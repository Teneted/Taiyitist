package org.bukkit.craftbukkit.block;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.registry.CraftOldEnumRegistryItem;
import org.jetbrains.annotations.NotNull;

public class CraftBiome extends CraftOldEnumRegistryItem<Biome, net.minecraft.world.level.biome.Biome> implements Biome {
   private static int count = 0;

   public static Biome minecraftToBukkit(net.minecraft.world.level.biome.Biome minecraft) {
      return (Biome)CraftRegistry.minecraftToBukkit(minecraft, Registries.BIOME, Registry.BIOME);
   }

   public static Biome minecraftHolderToBukkit(Holder<net.minecraft.world.level.biome.Biome> minecraft) {
      return minecraftToBukkit((net.minecraft.world.level.biome.Biome)minecraft.value());
   }

   public static net.minecraft.world.level.biome.Biome bukkitToMinecraft(Biome bukkit) {
      return bukkit == Biome.CUSTOM ? null : (net.minecraft.world.level.biome.Biome)CraftRegistry.bukkitToMinecraft(bukkit);
   }

   public static Holder<net.minecraft.world.level.biome.Biome> bukkitToMinecraftHolder(Biome bukkit) {
      if (bukkit == Biome.CUSTOM) {
         return null;
      } else {
         net.minecraft.core.Registry<net.minecraft.world.level.biome.Biome> registry = CraftRegistry.getMinecraftRegistry(Registries.BIOME);
         Holder var3 = registry.wrapAsHolder(bukkitToMinecraft(bukkit));
         if (var3 instanceof Holder.Reference) {
            Holder.Reference<net.minecraft.world.level.biome.Biome> holder = (Holder.Reference)var3;
            return holder;
         } else {
            throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkit) + ", this can happen if a plugin creates its own biome base with out properly registering it.");
         }
      }
   }

   public CraftBiome(NamespacedKey key, Holder<net.minecraft.world.level.biome.Biome> handle) {
      super(key, handle, count++);
   }

   @NotNull
   public NamespacedKey getKey() {
      return this.getKeyOrThrow();
   }
}
