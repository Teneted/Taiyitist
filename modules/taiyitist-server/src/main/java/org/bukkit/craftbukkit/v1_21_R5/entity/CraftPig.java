package org.bukkit.craftbukkit.v1_21_R5.entity;

import com.google.common.base.Preconditions;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.PigVariant;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.v1_21_R5.CraftRegistry;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.registry.CraftRegistryItem;
import org.bukkit.entity.Pig;

public class CraftPig extends CraftAnimals implements Pig {
   public CraftPig(CraftServer server, net.minecraft.world.entity.animal.Pig entity) {
      super(server, entity);
   }

   public boolean hasSaddle() {
      return this.getHandle().isSaddled();
   }

   public void setSaddle(boolean saddled) {
      this.getHandle().setItemSlot(EquipmentSlot.SADDLE, saddled ? new ItemStack(Items.SADDLE) : ItemStack.EMPTY);
   }

   public int getBoostTicks() {
      return this.getHandle().steering.boosting ? this.getHandle().steering.boostTimeTotal() : 0;
   }

   public void setBoostTicks(int ticks) {
      Preconditions.checkArgument(ticks >= 0, "ticks must be >= 0");
      this.getHandle().steering.setBoostTicks(ticks);
   }

   public int getCurrentBoostTicks() {
      return this.getHandle().steering.boosting ? this.getHandle().steering.boostTime : 0;
   }

   public void setCurrentBoostTicks(int ticks) {
      if (this.getHandle().steering.boosting) {
         int max = this.getHandle().steering.boostTimeTotal();
         Preconditions.checkArgument(ticks >= 0 && ticks <= max, "boost ticks must not exceed 0 or %d (inclusive)", max);
         this.getHandle().steering.boostTime = ticks;
      }
   }

   public Material getSteerMaterial() {
      return Material.CARROT_ON_A_STICK;
   }

   public Pig.Variant getVariant() {
      return CraftPig.CraftVariant.minecraftHolderToBukkit(this.getHandle().getVariant());
   }

   public void setVariant(Pig.Variant variant) {
      Preconditions.checkArgument(variant != null, "variant");
      this.getHandle().setVariant(CraftPig.CraftVariant.bukkitToMinecraftHolder(variant));
   }

   public net.minecraft.world.entity.animal.Pig getHandle() {
      return (net.minecraft.world.entity.animal.Pig)this.entity;
   }

   public String toString() {
      return "CraftPig";
   }

   public static class CraftVariant extends CraftRegistryItem<PigVariant> implements Pig.Variant {
      public static Pig.Variant minecraftToBukkit(PigVariant minecraft) {
         return (Pig.Variant)CraftRegistry.minecraftToBukkit(minecraft, Registries.PIG_VARIANT, Registry.PIG_VARIANT);
      }

      public static Pig.Variant minecraftHolderToBukkit(Holder<PigVariant> minecraft) {
         return minecraftToBukkit((PigVariant)minecraft.value());
      }

      public static PigVariant bukkitToMinecraft(Pig.Variant bukkit) {
         return (PigVariant)CraftRegistry.bukkitToMinecraft(bukkit);
      }

      public static Holder<PigVariant> bukkitToMinecraftHolder(Pig.Variant bukkit) {
         return CraftRegistry.bukkitToMinecraftHolder(bukkit, Registries.PIG_VARIANT);
      }

      public CraftVariant(NamespacedKey key, Holder<PigVariant> handle) {
         super(key, handle);
      }

      public NamespacedKey getKey() {
         return this.getKeyOrThrow();
      }
   }
}
