package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.GlowSquid;

public class CraftGlowSquid extends CraftSquid implements GlowSquid {
   public CraftGlowSquid(CraftServer server, net.minecraft.world.entity.GlowSquid entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.GlowSquid getHandle() {
      return (net.minecraft.world.entity.GlowSquid)super.getHandle();
   }

   public String toString() {
      return "CraftGlowSquid";
   }

   public int getDarkTicksRemaining() {
      return this.getHandle().getDarkTicksRemaining();
   }

   public void setDarkTicksRemaining(int darkTicksRemaining) {
      Preconditions.checkArgument(darkTicksRemaining >= 0, "darkTicksRemaining must be >= 0");
      this.getHandle().setDarkTicks(darkTicksRemaining);
   }
}
