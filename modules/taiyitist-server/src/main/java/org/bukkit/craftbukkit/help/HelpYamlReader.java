package org.bukkit.craftbukkit.help;

import com.google.common.base.Charsets;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.help.HelpTopic;

public class HelpYamlReader {
   private YamlConfiguration helpYaml;
   private final char ALT_COLOR_CODE = '&';
   private final Server server;

   public HelpYamlReader(Server server) {
      this.server = server;
      File helpYamlFile = new File("help.yml");
      YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("configurations/help.yml"), Charsets.UTF_8));

      try {
         this.helpYaml = YamlConfiguration.loadConfiguration(helpYamlFile);
         this.helpYaml.options().copyDefaults(true);
         this.helpYaml.setDefaults(defaultConfig);

         try {
            if (!helpYamlFile.exists()) {
               this.helpYaml.save(helpYamlFile);
            }
         } catch (IOException var5) {
            IOException ex = var5;
            server.getLogger().log(Level.SEVERE, "Could not save " + String.valueOf(helpYamlFile), ex);
         }
      } catch (Exception var6) {
         server.getLogger().severe("Failed to load help.yml. Verify the yaml indentation is correct. Reverting to default help.yml.");
         this.helpYaml = defaultConfig;
      }

   }

   public List<HelpTopic> getGeneralTopics() {
      List<HelpTopic> topics = new LinkedList();
      ConfigurationSection generalTopics = this.helpYaml.getConfigurationSection("general-topics");
      if (generalTopics != null) {
         Iterator var3 = generalTopics.getKeys(false).iterator();

         while(var3.hasNext()) {
            String topicName = (String)var3.next();
            ConfigurationSection section = generalTopics.getConfigurationSection(topicName);
            String shortText = ChatColor.translateAlternateColorCodes('&', section.getString("shortText", ""));
            String fullText = ChatColor.translateAlternateColorCodes('&', section.getString("fullText", ""));
            String permission = section.getString("permission", "");
            topics.add(new CustomHelpTopic(topicName, shortText, fullText, permission));
         }
      }

      return topics;
   }

   public List<HelpTopic> getIndexTopics() {
      List<HelpTopic> topics = new LinkedList();
      ConfigurationSection indexTopics = this.helpYaml.getConfigurationSection("index-topics");
      if (indexTopics != null) {
         Iterator var3 = indexTopics.getKeys(false).iterator();

         while(var3.hasNext()) {
            String topicName = (String)var3.next();
            ConfigurationSection section = indexTopics.getConfigurationSection(topicName);
            String shortText = ChatColor.translateAlternateColorCodes('&', section.getString("shortText", ""));
            String preamble = ChatColor.translateAlternateColorCodes('&', section.getString("preamble", ""));
            String permission = ChatColor.translateAlternateColorCodes('&', section.getString("permission", ""));
            List<String> commands = section.getStringList("commands");
            topics.add(new CustomIndexHelpTopic(this.server.getHelpMap(), topicName, shortText, permission, commands, preamble));
         }
      }

      return topics;
   }

   public List<HelpTopicAmendment> getTopicAmendments() {
      List<HelpTopicAmendment> amendments = new LinkedList();
      ConfigurationSection commandTopics = this.helpYaml.getConfigurationSection("amended-topics");
      if (commandTopics != null) {
         Iterator var3 = commandTopics.getKeys(false).iterator();

         while(var3.hasNext()) {
            String topicName = (String)var3.next();
            ConfigurationSection section = commandTopics.getConfigurationSection(topicName);
            String description = ChatColor.translateAlternateColorCodes('&', section.getString("shortText", ""));
            String usage = ChatColor.translateAlternateColorCodes('&', section.getString("fullText", ""));
            String permission = section.getString("permission", "");
            amendments.add(new HelpTopicAmendment(topicName, description, usage, permission));
         }
      }

      return amendments;
   }

   public List<String> getIgnoredPlugins() {
      return this.helpYaml.getStringList("ignore-plugins");
   }

   public boolean commandTopicsInMasterIndex() {
      return this.helpYaml.getBoolean("command-topics-in-master-index", true);
   }
}
