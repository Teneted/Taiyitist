package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.registry.CraftRegistryItem;
import org.bukkit.entity.Wolf;

public class CraftWolf extends CraftTameableAnimal implements Wolf {
   public CraftWolf(CraftServer server, net.minecraft.world.entity.animal.wolf.Wolf wolf) {
      super(server, wolf);
   }

   public boolean isAngry() {
      return this.getHandle().isAngry();
   }

   public void setAngry(boolean angry) {
      if (angry) {
         this.getHandle().startPersistentAngerTimer();
      } else {
         this.getHandle().stopBeingAngry();
      }

   }

   public net.minecraft.world.entity.animal.wolf.Wolf getHandle() {
      return (net.minecraft.world.entity.animal.wolf.Wolf)this.entity;
   }

   public DyeColor getCollarColor() {
      return DyeColor.getByWoolData((byte)this.getHandle().getCollarColor().getId());
   }

   public void setCollarColor(DyeColor color) {
      this.getHandle().setCollarColor(net.minecraft.world.item.DyeColor.byId(color.getWoolData()));
   }

   public boolean isWet() {
      return this.getHandle().isWet;
   }

   public float getTailAngle() {
      return this.getHandle().getTailAngle();
   }

   public boolean isInterested() {
      return this.getHandle().isInterested();
   }

   public void setInterested(boolean flag) {
      this.getHandle().setIsInterested(flag);
   }

   public Wolf.Variant getVariant() {
      return CraftWolf.CraftVariant.minecraftHolderToBukkit(this.getHandle().getVariant());
   }

   public void setVariant(Wolf.Variant variant) {
      Preconditions.checkArgument(variant != null, "variant");
      this.getHandle().setVariant(CraftWolf.CraftVariant.bukkitToMinecraftHolder(variant));
   }

   public static class CraftVariant extends CraftRegistryItem<WolfVariant> implements Wolf.Variant {
      public static Wolf.Variant minecraftToBukkit(WolfVariant minecraft) {
         return (Wolf.Variant)CraftRegistry.minecraftToBukkit(minecraft, Registries.WOLF_VARIANT, Registry.WOLF_VARIANT);
      }

      public static Wolf.Variant minecraftHolderToBukkit(Holder<WolfVariant> minecraft) {
         return minecraftToBukkit((WolfVariant)minecraft.value());
      }

      public static WolfVariant bukkitToMinecraft(Wolf.Variant bukkit) {
         return (WolfVariant)CraftRegistry.bukkitToMinecraft(bukkit);
      }

      public static Holder<WolfVariant> bukkitToMinecraftHolder(Wolf.Variant bukkit) {
         Preconditions.checkArgument(bukkit != null);
         net.minecraft.core.Registry<WolfVariant> registry = CraftRegistry.getMinecraftRegistry(Registries.WOLF_VARIANT);
         Holder var3 = registry.wrapAsHolder(bukkitToMinecraft(bukkit));
         if (var3 instanceof Holder.Reference<WolfVariant> holder) {
            return holder;
         } else {
            throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkit) + ", this can happen if a plugin creates its own wolf variant with out properly registering it.");
         }
      }

      public CraftVariant(NamespacedKey key, Holder<WolfVariant> handle) {
         super(key, handle);
      }

      public NamespacedKey getKey() {
         return this.getKeyOrThrow();
      }
   }
}
