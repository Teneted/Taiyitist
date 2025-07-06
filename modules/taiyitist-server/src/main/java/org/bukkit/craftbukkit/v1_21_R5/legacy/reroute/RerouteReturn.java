package org.bukkit.craftbukkit.v1_21_R5.legacy.reroute;

import org.objectweb.asm.Type;

public record RerouteReturn(Type type) {
   public RerouteReturn(Type type) {
      this.type = type;
   }

   public int instruction() {
      return this.type.getOpcode(172);
   }

   public Type type() {
      return this.type;
   }
}
