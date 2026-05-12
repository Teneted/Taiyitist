package org.spigotmc;

import org.teneted.taiyitist.bukkit.BukkitMethodHooks;

public class AsyncCatcher {
   public static boolean enabled = true;

   public static void catchOp(String reason) {
      if (enabled && Thread.currentThread() != BukkitMethodHooks.getServer().serverThread) {
         throw new IllegalStateException("Asynchronous " + reason + "!");
      }
   }
}
