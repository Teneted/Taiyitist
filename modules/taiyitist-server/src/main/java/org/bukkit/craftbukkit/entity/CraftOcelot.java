package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Ocelot;

public class CraftOcelot extends CraftAnimals implements Ocelot {
   public CraftOcelot(CraftServer server, net.minecraft.world.entity.animal.Ocelot ocelot) {
      super(server, ocelot);
   }

   public net.minecraft.world.entity.animal.Ocelot getHandle() {
      return (net.minecraft.world.entity.animal.Ocelot)this.entity;
   }

   public boolean isTrusting() {
      return this.getHandle().isTrusting();
   }

   public void setTrusting(boolean trust) {
      this.getHandle().setTrusting(trust);
   }

   public Ocelot.Type getCatType() {
      return Type.WILD_OCELOT;
   }

   public void setCatType(Ocelot.Type type) {
      throw new UnsupportedOperationException("Cats are now a different entity!");
   }

   public String toString() {
      return "CraftOcelot";
   }
}
