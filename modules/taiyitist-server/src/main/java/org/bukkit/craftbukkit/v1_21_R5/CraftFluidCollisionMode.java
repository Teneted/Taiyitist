package org.bukkit.craftbukkit.v1_21_R5;

import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Fluid;
import org.bukkit.FluidCollisionMode;

public final class CraftFluidCollisionMode {
   private CraftFluidCollisionMode() {
   }

   public static ClipContext.Fluid toNMS(FluidCollisionMode fluidCollisionMode) {
      if (fluidCollisionMode == null) {
         return null;
      } else {
         switch (fluidCollisionMode) {
            case ALWAYS -> {
               return Fluid.ANY;
            }
            case SOURCE_ONLY -> {
               return Fluid.SOURCE_ONLY;
            }
            case NEVER -> {
               return Fluid.NONE;
            }
            default -> {
               return null;
            }
         }
      }
   }
}
