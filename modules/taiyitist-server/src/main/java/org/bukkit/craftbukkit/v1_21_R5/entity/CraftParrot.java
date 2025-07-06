package org.bukkit.craftbukkit.v1_21_R5.entity;

import com.google.common.base.Preconditions;
import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Parrot.Variant;

public class CraftParrot extends CraftTameableAnimal implements Parrot {
   public CraftParrot(CraftServer server, net.minecraft.world.entity.animal.Parrot parrot) {
      super(server, parrot);
   }

   public net.minecraft.world.entity.animal.Parrot getHandle() {
      return (net.minecraft.world.entity.animal.Parrot)this.entity;
   }

   public Parrot.Variant getVariant() {
      return Variant.values()[this.getHandle().getVariant().ordinal()];
   }

   public void setVariant(Parrot.Variant variant) {
      Preconditions.checkArgument(variant != null, "variant");
      this.getHandle().setVariant(net.minecraft.world.entity.animal.Parrot.Variant.byId(variant.ordinal()));
   }

   public String toString() {
      return "CraftParrot";
   }

   public boolean isDancing() {
      return this.getHandle().isPartyParrot();
   }
}
