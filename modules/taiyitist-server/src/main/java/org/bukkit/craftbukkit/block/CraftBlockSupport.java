package org.bukkit.craftbukkit.block;

import net.minecraft.world.level.block.SupportType;
import org.bukkit.block.BlockSupport;

public final class CraftBlockSupport {
   private CraftBlockSupport() {
   }

   public static BlockSupport toBukkit(SupportType support) {
      BlockSupport var10000;
      switch (support) {
         case FULL -> var10000 = BlockSupport.FULL;
         case CENTER -> var10000 = BlockSupport.CENTER;
         case RIGID -> var10000 = BlockSupport.RIGID;
         default -> throw new IllegalArgumentException("Unsupported EnumBlockSupport type: " + String.valueOf(support) + ". This is a bug.");
      }

      return var10000;
   }

   public static SupportType toNMS(BlockSupport support) {
      SupportType var10000;
      switch (support) {
         case FULL -> var10000 = SupportType.FULL;
         case CENTER -> var10000 = SupportType.CENTER;
         case RIGID -> var10000 = SupportType.RIGID;
         default -> throw new IllegalArgumentException("Unsupported BlockSupport type: " + String.valueOf(support) + ". This is a bug.");
      }

      return var10000;
   }
}
