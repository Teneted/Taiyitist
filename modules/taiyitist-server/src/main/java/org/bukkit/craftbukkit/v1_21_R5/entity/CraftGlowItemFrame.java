package org.bukkit.craftbukkit.v1_21_R5.entity;

import org.bukkit.craftbukkit.v1_21_R5.CraftServer;
import org.bukkit.entity.GlowItemFrame;

public class CraftGlowItemFrame extends CraftItemFrame implements GlowItemFrame {
   public CraftGlowItemFrame(CraftServer server, net.minecraft.world.entity.decoration.GlowItemFrame entity) {
      super(server, entity);
   }

   public net.minecraft.world.entity.decoration.GlowItemFrame getHandle() {
      return (net.minecraft.world.entity.decoration.GlowItemFrame)super.getHandle();
   }

   public String toString() {
      String var10000 = String.valueOf(this.getItem());
      return "CraftGlowItemFrame{item=" + var10000 + ", rotation=" + String.valueOf(this.getRotation()) + "}";
   }
}
