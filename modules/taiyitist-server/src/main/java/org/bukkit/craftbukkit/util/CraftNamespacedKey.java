package org.bukkit.craftbukkit.util;

import net.minecraft.resources.ResourceLocation;
import org.bukkit.NamespacedKey;

public final class CraftNamespacedKey {
   public static NamespacedKey fromStringOrNull(String string) {
      if (string != null && !string.isEmpty()) {
         ResourceLocation minecraft = ResourceLocation.tryParse(string);
         return minecraft == null ? null : fromMinecraft(minecraft);
      } else {
         return null;
      }
   }

   public static NamespacedKey fromString(String string) {
      return fromMinecraft(ResourceLocation.parse(string));
   }

   public static NamespacedKey fromMinecraft(ResourceLocation minecraft) {
      return new NamespacedKey(minecraft.getNamespace(), minecraft.getPath());
   }

   public static ResourceLocation toMinecraft(NamespacedKey key) {
      return ResourceLocation.fromNamespaceAndPath(key.getNamespace(), key.getKey());
   }
}
