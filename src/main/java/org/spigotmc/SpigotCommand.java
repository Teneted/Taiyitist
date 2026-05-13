package org.spigotmc;

import java.io.File;
import java.util.Iterator;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.teneted.taiyitist.bukkit.BukkitMethodHooks;

public class SpigotCommand extends Command {
   public SpigotCommand(String name) {
      super(name);
      this.description = "Spigot related commands";
      this.usageMessage = "/spigot reload";
      this.setPermission("bukkit.command.spigot");
   }

   public boolean execute(CommandSender sender, String commandLabel, String[] args) {
      if (!this.testPermission(sender)) {
         return true;
      } else if (args.length != 1) {
         String var10001 = String.valueOf(ChatColor.RED);
         sender.sendMessage(var10001 + "Usage: " + this.usageMessage);
         return false;
      } else {
         if (args[0].equals("reload")) {
            Command.broadcastCommandMessage(sender, String.valueOf(ChatColor.RED) + "Please note that this command is not supported and may cause issues.");
            Command.broadcastCommandMessage(sender, String.valueOf(ChatColor.RED) + "If you encounter any issues please use the /stop command to restart your server.");
            MinecraftServer console = BukkitMethodHooks.getServer();
            SpigotConfig.init((File)console.bridge$options().valueOf("spigot-settings"));
            Iterator var5 = console.getAllLevels().iterator();

            while(var5.hasNext()) {
               ServerLevel world = (ServerLevel)var5.next();
               world.bridge$spigotConfig().init();
            }

            ++console.bridge$server().reloadCount;
            Command.broadcastCommandMessage(sender, String.valueOf(ChatColor.GREEN) + "Reload complete.");
         }

         return true;
      }
   }
}
