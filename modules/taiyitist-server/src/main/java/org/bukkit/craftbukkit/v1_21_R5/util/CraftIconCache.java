package org.bukkit.craftbukkit.v1_21_R5.util;

import org.bukkit.util.CachedServerIcon;

public class CraftIconCache implements CachedServerIcon {
   public final byte[] value;

   public CraftIconCache(byte[] value) {
      this.value = value;
   }
}
