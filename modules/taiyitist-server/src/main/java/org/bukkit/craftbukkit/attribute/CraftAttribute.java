package org.bukkit.craftbukkit.attribute;

import com.google.common.base.Preconditions;
import java.util.Locale;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.legacy.FieldRename;
import org.bukkit.craftbukkit.registry.CraftOldEnumRegistryItem;
import org.bukkit.craftbukkit.util.ApiVersion;
import org.jetbrains.annotations.NotNull;

public class CraftAttribute extends CraftOldEnumRegistryItem<Attribute, net.minecraft.world.entity.ai.attributes.Attribute> implements Attribute {
   private static int count = 0;

   public static Attribute minecraftToBukkit(net.minecraft.world.entity.ai.attributes.Attribute minecraft) {
      return (Attribute)CraftRegistry.minecraftToBukkit(minecraft, Registries.ATTRIBUTE, Registry.ATTRIBUTE);
   }

   public static Attribute minecraftHolderToBukkit(Holder<net.minecraft.world.entity.ai.attributes.Attribute> minecraft) {
      return minecraftToBukkit((net.minecraft.world.entity.ai.attributes.Attribute)minecraft.value());
   }

   public static Attribute stringToBukkit(String string) {
      Preconditions.checkArgument(string != null);
      string = FieldRename.convertAttributeName(ApiVersion.CURRENT, string);
      string = string.toLowerCase(Locale.ROOT);
      NamespacedKey key = NamespacedKey.fromString(string);
      return (Attribute)CraftRegistry.get(Registry.ATTRIBUTE, key, ApiVersion.CURRENT);
   }

   public static net.minecraft.world.entity.ai.attributes.Attribute bukkitToMinecraft(Attribute bukkit) {
      return (net.minecraft.world.entity.ai.attributes.Attribute)CraftRegistry.bukkitToMinecraft(bukkit);
   }

   public static Holder<net.minecraft.world.entity.ai.attributes.Attribute> bukkitToMinecraftHolder(Attribute bukkit) {
      Preconditions.checkArgument(bukkit != null);
      net.minecraft.core.Registry<net.minecraft.world.entity.ai.attributes.Attribute> registry = CraftRegistry.getMinecraftRegistry(Registries.ATTRIBUTE);
      if (registry.wrapAsHolder(bukkitToMinecraft(bukkit)) instanceof Holder.Reference<net.minecraft.world.entity.ai.attributes.Attribute> holder) {
         return holder;
      } else {
         throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkit) + ", this can happen if a plugin creates its own sound effect with out properly registering it.");
      }
   }

   public static String bukkitToString(Attribute bukkit) {
      Preconditions.checkArgument(bukkit != null);
      return bukkit.getKey().toString();
   }

   public CraftAttribute(NamespacedKey key, Holder<net.minecraft.world.entity.ai.attributes.Attribute> handle) {
      super(key, handle, count++);
   }

   @NotNull
   public NamespacedKey getKey() {
      return this.getKeyOrThrow();
   }

   @NotNull
   public String getTranslationKey() {
      return ((net.minecraft.world.entity.ai.attributes.Attribute)this.getHandle()).getDescriptionId();
   }
}
