package org.bukkit.craftbukkit.util;

import net.minecraft.server.MinecraftServer;
import org.spigotmc.AsyncCatcher;

public class ServerShutdownThread extends Thread {
   private final MinecraftServer server;

   public ServerShutdownThread(MinecraftServer server) {
      this.server = server;
   }

   public void run() {
      try {
         AsyncCatcher.enabled = false;
         this.server.close();
      } finally {
         org.apache.logging.log4j.LogManager.shutdown(); // Taiyitist
      }

   }
}
