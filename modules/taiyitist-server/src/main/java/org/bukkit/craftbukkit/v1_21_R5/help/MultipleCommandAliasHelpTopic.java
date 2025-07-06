package org.bukkit.craftbukkit.v1_21_R5.help;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.MultipleCommandAlias;
import org.bukkit.help.HelpTopic;

public class MultipleCommandAliasHelpTopic extends HelpTopic {
   private final MultipleCommandAlias alias;

   public MultipleCommandAliasHelpTopic(MultipleCommandAlias alias) {
      this.alias = alias;
      this.name = "/" + alias.getLabel();
      StringBuilder sb = new StringBuilder();

      String var10001;
      for(int i = 0; i < alias.getCommands().length; ++i) {
         if (i != 0) {
            var10001 = String.valueOf(ChatColor.GOLD);
            sb.append(var10001 + " > " + String.valueOf(ChatColor.WHITE));
         }

         sb.append("/");
         sb.append(alias.getCommands()[i].getLabel());
      }

      this.shortText = sb.toString();
      var10001 = String.valueOf(ChatColor.GOLD);
      this.fullText = var10001 + "Alias for: " + String.valueOf(ChatColor.WHITE) + this.getShortText();
   }

   public boolean canSee(CommandSender sender) {
      if (this.amendedPermission == null) {
         if (sender instanceof ConsoleCommandSender) {
            return true;
         } else {
            Command[] var2 = this.alias.getCommands();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Command command = var2[var4];
               if (!command.testPermissionSilent(sender)) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return sender.hasPermission(this.amendedPermission);
      }
   }
}
