package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Axolotl;

public class CraftAxolotl extends CraftAnimals implements Axolotl {
   public CraftAxolotl(CraftServer server, net.minecraft.world.entity.animal.axolotl.Axolotl entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.animal.axolotl.Axolotl getHandle() {
      return (net.minecraft.world.entity.animal.axolotl.Axolotl)super.getHandle();
   }

   public String toString() {
      return "CraftAxolotl";
   }

   public boolean isPlayingDead() {
      return this.getHandle().isPlayingDead();
   }

   public void setPlayingDead(boolean playingDead) {
      this.getHandle().setPlayingDead(playingDead);
   }

   public Axolotl.Variant getVariant() {
      return Variant.values()[this.getHandle().getVariant().ordinal()];
   }

   public void setVariant(Axolotl.Variant variant) {
      Preconditions.checkArgument(variant != null, "variant");
      this.getHandle().setVariant(net.minecraft.world.entity.animal.axolotl.Axolotl.Variant.byId(variant.ordinal()));
   }
}
