package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Salmon;

public class CraftSalmon extends CraftFish implements Salmon {
   public CraftSalmon(CraftServer server, net.minecraft.world.entity.animal.Salmon entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.Salmon getHandle() {
      return (net.minecraft.world.entity.animal.Salmon)super.getHandle();
   }

   public String toString() {
      return "CraftSalmon";
   }

   public Salmon.Variant getVariant() {
      return Variant.values()[this.getHandle().getVariant().ordinal()];
   }

   public void setVariant(Salmon.Variant variant) {
      Preconditions.checkArgument(variant != null, "variant");
      this.getHandle().setVariant(net.minecraft.world.entity.animal.Salmon.Variant.values()[variant.ordinal()]);
   }
}
