package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.animal.CowVariant;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.registry.CraftRegistryItem;
import org.bukkit.entity.Cow;

public class CraftCow extends CraftAbstractCow implements Cow {
   public CraftCow(CraftServer server, net.minecraft.world.entity.animal.Cow entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.Cow getHandle() {
      return (net.minecraft.world.entity.animal.Cow)this.entity;
   }

   public String toString() {
      return "CraftCow";
   }

   public Cow.Variant getVariant() {
      return CraftCow.CraftVariant.minecraftHolderToBukkit(this.getHandle().getVariant());
   }

   public void setVariant(Cow.Variant variant) {
      Preconditions.checkArgument(variant != null, "variant");
      this.getHandle().setVariant(CraftCow.CraftVariant.bukkitToMinecraftHolder(variant));
   }

   public static class CraftVariant extends CraftRegistryItem<CowVariant> implements Cow.Variant {
      public static Cow.Variant minecraftToBukkit(CowVariant minecraft) {
         return (Cow.Variant)CraftRegistry.minecraftToBukkit(minecraft, Registries.COW_VARIANT, Registry.COW_VARIANT);
      }

      public static Cow.Variant minecraftHolderToBukkit(Holder<CowVariant> minecraft) {
         return minecraftToBukkit((CowVariant)minecraft.value());
      }

      public static CowVariant bukkitToMinecraft(Cow.Variant bukkit) {
         return (CowVariant)CraftRegistry.bukkitToMinecraft(bukkit);
      }

      public static Holder<CowVariant> bukkitToMinecraftHolder(Cow.Variant bukkit) {
         return CraftRegistry.bukkitToMinecraftHolder(bukkit, Registries.COW_VARIANT);
      }

      public CraftVariant(NamespacedKey key, Holder<CowVariant> handle) {
         super(key, handle);
      }

      public NamespacedKey getKey() {
         return this.getKeyOrThrow();
      }
   }
}
