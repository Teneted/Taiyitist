package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import java.util.Locale;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.legacy.FieldRename;
import org.bukkit.craftbukkit.util.ApiVersion;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.EntityType;

public class CraftEntityType {
   public static EntityType minecraftToBukkit(net.minecraft.world.entity.EntityType<?> minecraft) {
      Preconditions.checkArgument(minecraft != null);
      Registry<net.minecraft.world.entity.EntityType<?>> registry = CraftRegistry.getMinecraftRegistry(Registries.ENTITY_TYPE);
      EntityType bukkit = (EntityType)org.bukkit.Registry.ENTITY_TYPE.get(CraftNamespacedKey.fromMinecraft(((ResourceKey)registry.getResourceKey(minecraft).orElseThrow()).location()));
      Preconditions.checkArgument(bukkit != null);
      return bukkit;
   }

   public static net.minecraft.world.entity.EntityType<?> bukkitToMinecraft(EntityType bukkit) {
      Preconditions.checkArgument(bukkit != null);
      return (net.minecraft.world.entity.EntityType)CraftRegistry.getMinecraftRegistry(Registries.ENTITY_TYPE).getOptional(CraftNamespacedKey.toMinecraft(bukkit.getKey())).orElseThrow();
   }

   public static Holder<net.minecraft.world.entity.EntityType<?>> bukkitToMinecraftHolder(EntityType bukkit) {
      Preconditions.checkArgument(bukkit != null);
      Registry<net.minecraft.world.entity.EntityType<?>> registry = CraftRegistry.getMinecraftRegistry(Registries.ENTITY_TYPE);
      Holder var3 = registry.wrapAsHolder(bukkitToMinecraft(bukkit));
      if (var3 instanceof Holder.Reference<net.minecraft.world.entity.EntityType<?>> holder) {
         return holder;
      } else {
         throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkit) + ", this can happen if a plugin creates its own sound effect with out properly registering it.");
      }
   }

   public static String bukkitToString(EntityType bukkit) {
      Preconditions.checkArgument(bukkit != null);
      return bukkit.getKey().toString();
   }

   public static EntityType stringToBukkit(String string) {
      Preconditions.checkArgument(string != null);
      string = FieldRename.convertEntityTypeName(ApiVersion.CURRENT, string);
      string = string.toLowerCase(Locale.ROOT);
      NamespacedKey key = NamespacedKey.fromString(string);
      return (EntityType)CraftRegistry.get(org.bukkit.Registry.ENTITY_TYPE, key, ApiVersion.CURRENT);
   }
}
