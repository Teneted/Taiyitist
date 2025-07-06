package org.bukkit.craftbukkit.v1_21_R5.entity;

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.animal.horse.Llama.Variant;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftInventoryLlama;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Llama.Color;
import org.bukkit.inventory.LlamaInventory;

public class CraftLlama extends CraftChestedHorse implements Llama {
   public CraftLlama(CraftServer server, net.minecraft.world.entity.animal.horse.Llama entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.horse.Llama getHandle() {
      return (net.minecraft.world.entity.animal.horse.Llama)super.getHandle();
   }

   public Llama.Color getColor() {
      return Color.values()[this.getHandle().getVariant().ordinal()];
   }

   public void setColor(Llama.Color color) {
      Preconditions.checkArgument(color != null, "color");
      this.getHandle().setVariant(Variant.byId(color.ordinal()));
   }

   public LlamaInventory getInventory() {
      return new CraftInventoryLlama(this.getHandle().inventory, this.getHandle().equipment);
   }

   public int getStrength() {
      return this.getHandle().getStrength();
   }

   public void setStrength(int strength) {
      Preconditions.checkArgument(1 <= strength && strength <= 5, "strength must be [1,5]");
      if (strength != this.getStrength()) {
         this.getHandle().setStrengthPublic(strength);
         this.getHandle().createInventory();
      }
   }

   public Horse.Variant getVariant() {
      return org.bukkit.entity.Horse.Variant.LLAMA;
   }

   public String toString() {
      return "CraftLlama";
   }
}
