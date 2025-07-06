package org.spigotmc;

import com.taiyitistmc.bukkit.BukkitMethodHooks;
import net.minecraft.server.MinecraftServer;

public class AsyncCatcher {
   public static boolean enabled = true;

   public static void catchOp(String reason) {
      if (enabled && Thread.currentThread() != BukkitMethodHooks.getServer().serverThread) {
         throw new IllegalStateException("Asynchronous " + reason + "!");
      }
   }
}
