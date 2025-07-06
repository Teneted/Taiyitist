package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.animal.CatVariant;
import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.registry.CraftOldEnumRegistryItem;
import org.bukkit.entity.Cat;

public class CraftCat extends CraftTameableAnimal implements Cat {
   public CraftCat(CraftServer server, net.minecraft.world.entity.animal.Cat entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.Cat getHandle() {
      return (net.minecraft.world.entity.animal.Cat)super.getHandle();
   }

   public String toString() {
      return "CraftCat";
   }

   public Cat.Type getCatType() {
      return CraftCat.CraftType.minecraftHolderToBukkit(this.getHandle().getVariant());
   }

   public void setCatType(Cat.Type type) {
      Preconditions.checkArgument(type != null, "Cannot have null Type");
      this.getHandle().setVariant(CraftCat.CraftType.bukkitToMinecraftHolder(type));
   }

   public DyeColor getCollarColor() {
      return DyeColor.getByWoolData((byte)this.getHandle().getCollarColor().getId());
   }

   public void setCollarColor(DyeColor color) {
      this.getHandle().setCollarColor(net.minecraft.world.item.DyeColor.byId(color.getWoolData()));
   }

   public static class CraftType extends CraftOldEnumRegistryItem<Cat.Type, CatVariant> implements Cat.Type {
      private static int count = 0;

      public static Cat.Type minecraftToBukkit(CatVariant minecraft) {
         return (Cat.Type)CraftRegistry.minecraftToBukkit(minecraft, Registries.CAT_VARIANT, Registry.CAT_VARIANT);
      }

      public static Cat.Type minecraftHolderToBukkit(Holder<CatVariant> minecraft) {
         return minecraftToBukkit((CatVariant)minecraft.value());
      }

      public static CatVariant bukkitToMinecraft(Cat.Type bukkit) {
         return (CatVariant)CraftRegistry.bukkitToMinecraft(bukkit);
      }

      public static Holder<CatVariant> bukkitToMinecraftHolder(Cat.Type bukkit) {
         return CraftRegistry.bukkitToMinecraftHolder(bukkit, Registries.CAT_VARIANT);
      }

      public CraftType(NamespacedKey key, Holder<CatVariant> handle) {
         super(key, handle, count++);
      }

      public NamespacedKey getKey() {
         return this.getKeyOrThrow();
      }
   }
}
