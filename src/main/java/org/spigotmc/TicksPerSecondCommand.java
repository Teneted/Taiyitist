package org.spigotmc;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.teneted.taiyitist.bukkit.BukkitMethodHooks;

public class TicksPerSecondCommand extends Command {
   public TicksPerSecondCommand(String name) {
      super(name);
      this.description = "Gets the current ticks per second for the server";
      this.usageMessage = "/tps";
      this.setPermission("bukkit.command.tps");
   }

   public boolean execute(CommandSender sender, String currentAlias, String[] args) {
      if (!this.testPermission(sender)) {
         return true;
      } else {
         StringBuilder sb = new StringBuilder(String.valueOf(ChatColor.GOLD) + "TPS from last 1m, 5m, 15m: ");
         double[] var5 = BukkitMethodHooks.getServer().getTPS();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            double tps = var5[var7];
            sb.append(this.format(tps));
            sb.append(", ");
         }

         sender.sendMessage(sb.substring(0, sb.length() - 2));
         String var10001 = String.valueOf(ChatColor.GOLD);
         sender.sendMessage(var10001 + "Current Memory Usage: " + String.valueOf(ChatColor.GREEN) + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576L + "/" + Runtime.getRuntime().totalMemory() / 1048576L + " mb (Max: " + Runtime.getRuntime().maxMemory() / 1048576L + " mb)");
         return true;
      }
   }

   private String format(double tps) {
      String var10000 = (tps > 18.0 ? ChatColor.GREEN : (tps > 16.0 ? ChatColor.YELLOW : ChatColor.RED)).toString();
      return var10000 + (tps > 20.0 ? "*" : "") + Math.min((double)Math.round(tps * 100.0) / 100.0, 20.0);
   }
}
