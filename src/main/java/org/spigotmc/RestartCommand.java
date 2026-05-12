package org.spigotmc;

import java.io.File;
import java.util.Iterator;
import java.util.Locale;

import com.taiyitistmc.bukkit.BukkitMethodHooks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.util.CraftChatMessage;

public class RestartCommand extends Command {
   public static boolean restarting;

   public RestartCommand(String name) {
      super(name);
      this.description = "Restarts the server";
      this.usageMessage = "/restart";
      this.setPermission("bukkit.command.restart");
   }

   public boolean execute(CommandSender sender, String currentAlias, String[] args) {
      if (this.testPermission(sender)) {
         BukkitMethodHooks.getServer().bridge$processQueue().add(new Runnable() {
            public void run() {
               RestartCommand.restart();
            }
         });
      }

      return true;
   }

   public static void restart() {
      restart(SpigotConfig.restartScript);
   }

   private static void restart(final String restartScript) {
      if (!restarting) {
         restarting = true;
         AsyncCatcher.enabled = false;

         try {
            String[] split = restartScript.split(" ");
            if (split.length > 0 && (new File(split[0])).isFile()) {
               System.out.println("Attempting to restart with " + restartScript);
               WatchdogThread.doStop();
               Iterator var2 = BukkitMethodHooks.getServer().getPlayerList().players.iterator();

               while(var2.hasNext()) {
                  ServerPlayer p = (ServerPlayer)var2.next();
                  p.connection.disconnect(CraftChatMessage.fromStringOrEmpty(SpigotConfig.restartMessage, true));
               }

               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var7) {
               }

               BukkitMethodHooks.getServer().getConnection().stop();

               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var6) {
               }

               try {
                  BukkitMethodHooks.getServer().close();
               } catch (Throwable var5) {
               }

               Thread shutdownHook = new Thread() {
                  public void run() {
                     try {
                        String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
                        if (os.contains("win")) {
                           Runtime.getRuntime().exec("cmd /c start " + restartScript);
                        } else {
                           Runtime.getRuntime().exec("sh " + restartScript);
                        }
                     } catch (Exception var2) {
                        Exception e = var2;
                        e.printStackTrace();
                     }

                  }
               };
               shutdownHook.setDaemon(true);
               Runtime.getRuntime().addShutdownHook(shutdownHook);
            } else {
               System.out.println("Startup script '" + SpigotConfig.restartScript + "' does not exist! Stopping server.");

               try {
                  BukkitMethodHooks.getServer().close();
               } catch (Throwable var4) {
               }
            }

            System.exit(0);
         } catch (Exception var8) {
            Exception ex = var8;
            ex.printStackTrace();
         }

      }
   }
}
