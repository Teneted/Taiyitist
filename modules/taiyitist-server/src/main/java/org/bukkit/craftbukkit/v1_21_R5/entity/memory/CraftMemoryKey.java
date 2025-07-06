package org.bukkit.craftbukkit.v1_21_R5.entity.memory;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.bukkit.craftbukkit.v1_21_R5.CraftRegistry;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftNamespacedKey;
import org.bukkit.entity.memory.MemoryKey;

public final class CraftMemoryKey {
   private CraftMemoryKey() {
   }

   public static <T, U> MemoryKey<U> minecraftToBukkit(MemoryModuleType<T> minecraft) {
      if (minecraft == null) {
         return null;
      } else {
         Registry<MemoryModuleType<?>> registry = CraftRegistry.getMinecraftRegistry(Registries.MEMORY_MODULE_TYPE);
         MemoryKey<U> bukkit = (MemoryKey)org.bukkit.Registry.MEMORY_MODULE_TYPE.get(CraftNamespacedKey.fromMinecraft(((ResourceKey)registry.getResourceKey(minecraft).orElseThrow()).location()));
         return bukkit;
      }
   }

   public static <T, U> MemoryModuleType<U> bukkitToMinecraft(MemoryKey<T> bukkit) {
      return bukkit == null ? null : (MemoryModuleType)CraftRegistry.getMinecraftRegistry(Registries.MEMORY_MODULE_TYPE).getOptional(CraftNamespacedKey.toMinecraft(bukkit.getKey())).orElseThrow();
   }
}
