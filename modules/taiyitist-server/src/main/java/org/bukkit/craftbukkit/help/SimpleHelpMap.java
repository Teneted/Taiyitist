package org.bukkit.craftbukkit.help;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.MultipleCommandAlias;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.command.VanillaCommandWrapper;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpMap;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicComparator;
import org.bukkit.help.HelpTopicFactory;
import org.bukkit.help.IndexHelpTopic;

public class SimpleHelpMap implements HelpMap {
   private HelpTopic defaultTopic;
   private final Map<String, HelpTopic> helpTopics = new TreeMap(HelpTopicComparator.topicNameComparatorInstance());
   private final Map<Class, HelpTopicFactory<Command>> topicFactoryMap = new HashMap();
   private final CraftServer server;
   private HelpYamlReader yaml;

   public SimpleHelpMap(CraftServer server) {
      this.server = server;
      this.yaml = new HelpYamlReader(server);
      Predicate indexFilter = Predicates.not(Predicates.instanceOf(CommandAliasHelpTopic.class));
      if (!this.yaml.commandTopicsInMasterIndex()) {
         indexFilter = Predicates.and(indexFilter, Predicates.not(new IsCommandTopicPredicate(this)));
      }

      this.defaultTopic = new IndexHelpTopic("Index", (String)null, (String)null, Collections2.filter(this.helpTopics.values(), indexFilter), "Use /help [n] to get page n of help.");
      this.registerHelpTopicFactory(MultipleCommandAlias.class, new MultipleCommandAliasHelpTopicFactory());
   }

   public synchronized HelpTopic getHelpTopic(String topicName) {
      if (topicName.equals("")) {
         return this.defaultTopic;
      } else {
         return this.helpTopics.containsKey(topicName) ? (HelpTopic)this.helpTopics.get(topicName) : null;
      }
   }

   public Collection<HelpTopic> getHelpTopics() {
      return this.helpTopics.values();
   }

   public synchronized void addTopic(HelpTopic topic) {
      if (!this.helpTopics.containsKey(topic.getName())) {
         this.helpTopics.put(topic.getName(), topic);
      }

   }

   public synchronized void clear() {
      this.helpTopics.clear();
   }

   public List<String> getIgnoredPlugins() {
      return this.yaml.getIgnoredPlugins();
   }

   public synchronized void initializeGeneralTopics() {
      this.yaml = new HelpYamlReader(this.server);
      Iterator var1 = this.yaml.getGeneralTopics().iterator();

      HelpTopic topic;
      while(var1.hasNext()) {
         topic = (HelpTopic)var1.next();
         this.addTopic(topic);
      }

      var1 = this.yaml.getIndexTopics().iterator();

      while(var1.hasNext()) {
         topic = (HelpTopic)var1.next();
         if (topic.getName().equals("Default")) {
            this.defaultTopic = topic;
         } else {
            this.addTopic(topic);
         }
      }

   }

   public synchronized void initializeCommands() {
      Set<String> ignoredPlugins = new HashSet(this.yaml.getIgnoredPlugins());
      if (!ignoredPlugins.contains("All")) {
         Iterator var2 = this.server.getCommandMap().getCommands().iterator();

         while(true) {
            label90:
            while(true) {
               Command command;
               Iterator var4;
               do {
                  if (!var2.hasNext()) {
                     var2 = this.server.getCommandMap().getCommands().iterator();

                     while(true) {
                        do {
                           if (!var2.hasNext()) {
                              Collection<HelpTopic> filteredTopics = Collections2.filter(this.helpTopics.values(), Predicates.instanceOf(CommandAliasHelpTopic.class));
                              if (!filteredTopics.isEmpty()) {
                                 this.addTopic(new IndexHelpTopic("Aliases", "Lists command aliases", (String)null, filteredTopics));
                              }

                              Map<String, Set<HelpTopic>> pluginIndexes = new HashMap();
                              this.fillPluginIndexes(pluginIndexes, this.server.getCommandMap().getCommands());
                              var4 = pluginIndexes.entrySet().iterator();

                              while(var4.hasNext()) {
                                 Map.Entry<String, Set<HelpTopic>> entry = (Map.Entry)var4.next();
                                 this.addTopic(new IndexHelpTopic((String)entry.getKey(), "All commands for " + (String)entry.getKey(), (String)null, (Collection)entry.getValue(), "Below is a list of all " + (String)entry.getKey() + " commands:"));
                              }

                              var4 = this.yaml.getTopicAmendments().iterator();

                              while(var4.hasNext()) {
                                 HelpTopicAmendment amendment = (HelpTopicAmendment)var4.next();
                                 if (this.helpTopics.containsKey(amendment.getTopicName())) {
                                    ((HelpTopic)this.helpTopics.get(amendment.getTopicName())).amendTopic(amendment.getShortText(), amendment.getFullText());
                                    if (amendment.getPermission() != null) {
                                       ((HelpTopic)this.helpTopics.get(amendment.getTopicName())).amendCanSee(amendment.getPermission());
                                    }
                                 }
                              }

                              return;
                           }

                           command = (Command)var2.next();
                        } while(this.commandInIgnoredPlugin(command, ignoredPlugins));

                        var4 = command.getAliases().iterator();

                        while(var4.hasNext()) {
                           String alias = (String)var4.next();
                           if (this.server.getCommandMap().getCommand(alias) == command) {
                              this.addTopic(new CommandAliasHelpTopic("/" + alias, "/" + command.getLabel(), this));
                           }
                        }
                     }
                  }

                  command = (Command)var2.next();
               } while(this.commandInIgnoredPlugin(command, ignoredPlugins));

               var4 = this.topicFactoryMap.keySet().iterator();

               Class c;
               HelpTopic t;
               do {
                  if (!var4.hasNext()) {
                     this.addTopic(new GenericCommandHelpTopic(command));
                     continue label90;
                  }

                  c = (Class)var4.next();
                  if (c.isAssignableFrom(command.getClass())) {
                     t = ((HelpTopicFactory)this.topicFactoryMap.get(c)).createTopic(command);
                     if (t != null) {
                        this.addTopic(t);
                     }
                     continue label90;
                  }
               } while(!(command instanceof PluginCommand) || !c.isAssignableFrom(((PluginCommand)command).getExecutor().getClass()));

               t = ((HelpTopicFactory)this.topicFactoryMap.get(c)).createTopic(command);
               if (t != null) {
                  this.addTopic(t);
               }
            }
         }
      }
   }

   private void fillPluginIndexes(Map<String, Set<HelpTopic>> pluginIndexes, Collection<? extends Command> commands) {
      Iterator var3 = commands.iterator();

      while(var3.hasNext()) {
         Command command = (Command)var3.next();
         String pluginName = this.getCommandPluginName(command);
         if (pluginName != null) {
            HelpTopic topic = this.getHelpTopic("/" + command.getLabel());
            if (topic != null) {
               if (!pluginIndexes.containsKey(pluginName)) {
                  pluginIndexes.put(pluginName, new TreeSet(HelpTopicComparator.helpTopicComparatorInstance()));
               }

               ((Set)pluginIndexes.get(pluginName)).add(topic);
            }
         }
      }

   }

   private String getCommandPluginName(Command command) {
      if (command instanceof VanillaCommandWrapper) {
         return "Minecraft";
      } else if (command instanceof BukkitCommand) {
         return "Bukkit";
      } else {
         return command instanceof PluginIdentifiableCommand ? ((PluginIdentifiableCommand)command).getPlugin().getName() : null;
      }
   }

   private boolean commandInIgnoredPlugin(Command command, Set<String> ignoredPlugins) {
      if (command instanceof BukkitCommand && ignoredPlugins.contains("Bukkit")) {
         return true;
      } else {
         return command instanceof PluginIdentifiableCommand && ignoredPlugins.contains(((PluginIdentifiableCommand)command).getPlugin().getName());
      }
   }

   public void registerHelpTopicFactory(Class commandClass, HelpTopicFactory factory) {
      Preconditions.checkArgument(Command.class.isAssignableFrom(commandClass) || CommandExecutor.class.isAssignableFrom(commandClass), "commandClass (%s) must implement either Command or CommandExecutor", commandClass.getName());
      this.topicFactoryMap.put(commandClass, factory);
   }

   private class IsCommandTopicPredicate implements Predicate<HelpTopic> {
      private IsCommandTopicPredicate(final SimpleHelpMap var1) {
      }

      public boolean apply(HelpTopic topic) {
         return topic.getName().charAt(0) == '/';
      }
   }
}
