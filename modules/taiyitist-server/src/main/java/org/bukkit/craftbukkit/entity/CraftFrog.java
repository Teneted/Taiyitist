package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.animal.frog.FrogVariant;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.registry.CraftOldEnumRegistryItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Frog;

public class CraftFrog extends CraftAnimals implements Frog {
   public CraftFrog(CraftServer server, net.minecraft.world.entity.animal.frog.Frog entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.frog.Frog getHandle() {
      return (net.minecraft.world.entity.animal.frog.Frog)this.entity;
   }

   public String toString() {
      return "CraftFrog";
   }

   public Entity getTongueTarget() {
      return (Entity)this.getHandle().getTongueTarget().map(net.minecraft.world.entity.Entity::getBukkitEntity).orElse((Object)null);
   }

   public void setTongueTarget(Entity target) {
      if (target == null) {
         this.getHandle().eraseTongueTarget();
      } else {
         this.getHandle().setTongueTarget(((CraftEntity)target).getHandle());
      }

   }

   public Frog.Variant getVariant() {
      return CraftFrog.CraftVariant.minecraftHolderToBukkit(this.getHandle().getVariant());
   }

   public void setVariant(Frog.Variant variant) {
      Preconditions.checkArgument(variant != null, "variant");
      this.getHandle().setVariant(CraftFrog.CraftVariant.bukkitToMinecraftHolder(variant));
   }

   public static class CraftVariant extends CraftOldEnumRegistryItem<Frog.Variant, FrogVariant> implements Frog.Variant {
      private static int count = 0;

      public static Frog.Variant minecraftToBukkit(FrogVariant minecraft) {
         return (Frog.Variant)CraftRegistry.minecraftToBukkit(minecraft, Registries.FROG_VARIANT, Registry.FROG_VARIANT);
      }

      public static Frog.Variant minecraftHolderToBukkit(Holder<FrogVariant> minecraft) {
         return minecraftToBukkit((FrogVariant)minecraft.value());
      }

      public static FrogVariant bukkitToMinecraft(Frog.Variant bukkit) {
         return (FrogVariant)CraftRegistry.bukkitToMinecraft(bukkit);
      }

      public static Holder<FrogVariant> bukkitToMinecraftHolder(Frog.Variant bukkit) {
         return CraftRegistry.bukkitToMinecraftHolder(bukkit, Registries.FROG_VARIANT);
      }

      public CraftVariant(NamespacedKey key, Holder<FrogVariant> handle) {
         super(key, handle, count++);
      }

      public NamespacedKey getKey() {
         return this.getKeyOrThrow();
      }
   }
}
