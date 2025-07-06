package org.bukkit.craftbukkit.v1_21_R5.entity;

import com.google.common.base.Preconditions;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.animal.ChickenVariant;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.v1_21_R5.CraftRegistry;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.registry.CraftRegistryItem;
import org.bukkit.entity.Chicken;

public class CraftChicken extends CraftAnimals implements Chicken {
   public CraftChicken(CraftServer server, net.minecraft.world.entity.animal.Chicken entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.Chicken getHandle() {
      return (net.minecraft.world.entity.animal.Chicken)this.entity;
   }

   public String toString() {
      return "CraftChicken";
   }

   public Chicken.Variant getVariant() {
      return CraftChicken.CraftVariant.minecraftHolderToBukkit(this.getHandle().getVariant());
   }

   public void setVariant(Chicken.Variant variant) {
      Preconditions.checkArgument(variant != null, "variant");
      this.getHandle().setVariant(CraftChicken.CraftVariant.bukkitToMinecraftHolder(variant));
   }

   public static class CraftVariant extends CraftRegistryItem<ChickenVariant> implements Chicken.Variant {
      public static Chicken.Variant minecraftToBukkit(ChickenVariant minecraft) {
         return (Chicken.Variant)CraftRegistry.minecraftToBukkit(minecraft, Registries.CHICKEN_VARIANT, Registry.CHICKEN_VARIANT);
      }

      public static Chicken.Variant minecraftHolderToBukkit(Holder<ChickenVariant> minecraft) {
         return minecraftToBukkit((ChickenVariant)minecraft.value());
      }

      public static ChickenVariant bukkitToMinecraft(Chicken.Variant bukkit) {
         return (ChickenVariant)CraftRegistry.bukkitToMinecraft(bukkit);
      }

      public static Holder<ChickenVariant> bukkitToMinecraftHolder(Chicken.Variant bukkit) {
         return CraftRegistry.bukkitToMinecraftHolder(bukkit, Registries.CHICKEN_VARIANT);
      }

      public CraftVariant(NamespacedKey key, Holder<ChickenVariant> handle) {
         super(key, handle);
      }

      public NamespacedKey getKey() {
         return this.getKeyOrThrow();
      }
   }
}
