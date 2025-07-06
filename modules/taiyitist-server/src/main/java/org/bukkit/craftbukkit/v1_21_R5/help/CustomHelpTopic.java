package org.bukkit.craftbukkit.v1_21_R5.help;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.help.HelpTopic;

public class CustomHelpTopic extends HelpTopic {
   private final String permissionNode;

   public CustomHelpTopic(String name, String shortText, String fullText, String permissionNode) {
      this.permissionNode = permissionNode;
      this.name = name;
      this.shortText = shortText;
      this.fullText = shortText + "\n" + fullText;
   }

   public boolean canSee(CommandSender sender) {
      if (sender instanceof ConsoleCommandSender) {
         return true;
      } else {
         return !this.permissionNode.equals("") ? sender.hasPermission(this.permissionNode) : true;
      }
   }
}
