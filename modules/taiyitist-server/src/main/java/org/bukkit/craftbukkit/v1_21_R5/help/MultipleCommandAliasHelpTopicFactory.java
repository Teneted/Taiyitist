package org.bukkit.craftbukkit.v1_21_R5.help;

import org.bukkit.command.MultipleCommandAlias;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicFactory;

public class MultipleCommandAliasHelpTopicFactory implements HelpTopicFactory<MultipleCommandAlias> {
   public HelpTopic createTopic(MultipleCommandAlias multipleCommandAlias) {
      return new MultipleCommandAliasHelpTopic(multipleCommandAlias);
   }
}
