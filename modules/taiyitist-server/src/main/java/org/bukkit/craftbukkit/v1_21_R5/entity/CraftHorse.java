package org.bukkit.craftbukkit.v1_21_R5.entity;

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.animal.horse.Markings;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftInventoryHorse;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.inventory.HorseInventory;

public class CraftHorse extends CraftAbstractHorse implements Horse {
   public CraftHorse(CraftServer server, net.minecraft.world.entity.animal.horse.Horse entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.horse.Horse getHandle() {
      return (net.minecraft.world.entity.animal.horse.Horse)super.getHandle();
   }

   public Horse.Variant getVariant() {
      return Variant.HORSE;
   }

   public Horse.Color getColor() {
      return Color.values()[this.getHandle().getVariant().getId()];
   }

   public void setColor(Horse.Color color) {
      Preconditions.checkArgument(color != null, "Color cannot be null");
      this.getHandle().setVariantAndMarkings(net.minecraft.world.entity.animal.horse.Variant.byId(color.ordinal()), this.getHandle().getMarkings());
   }

   public Horse.Style getStyle() {
      return Style.values()[this.getHandle().getMarkings().getId()];
   }

   public void setStyle(Horse.Style style) {
      Preconditions.checkArgument(style != null, "Style cannot be null");
      this.getHandle().setVariantAndMarkings(this.getHandle().getVariant(), Markings.byId(style.ordinal()));
   }

   public boolean isCarryingChest() {
      return false;
   }

   public void setCarryingChest(boolean chest) {
      throw new UnsupportedOperationException("Not supported.");
   }

   public HorseInventory getInventory() {
      return new CraftInventoryHorse(this.getHandle().inventory, this.getHandle().equipment);
   }

   public String toString() {
      String var10000 = String.valueOf(this.getVariant());
      return "CraftHorse{variant=" + var10000 + ", owner=" + String.valueOf(this.getOwner()) + "}";
   }
}
