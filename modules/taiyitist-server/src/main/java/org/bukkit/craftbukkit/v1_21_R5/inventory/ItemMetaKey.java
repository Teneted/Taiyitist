package org.bukkit.craftbukkit.v1_21_R5.inventory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ItemMetaKey {
   public final String BUKKIT;
   public final String NBT;

   public ItemMetaKey(String both) {
      this(both, both);
   }

   public ItemMetaKey(String nbt, String bukkit) {
      this.NBT = nbt;
      this.BUKKIT = bukkit;
   }

   @Retention(RetentionPolicy.SOURCE)
   @Target({ElementType.FIELD})
   @interface Specific {
      To value();

      public static enum To {
         BUKKIT,
         NBT;

         // $FF: synthetic method
         private static To[] $values() {
            return new To[]{BUKKIT, NBT};
         }
      }
   }
}
