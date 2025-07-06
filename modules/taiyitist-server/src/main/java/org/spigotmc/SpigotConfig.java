package org.spigotmc;

import com.google.common.base.Throwables;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import com.taiyitistmc.bukkit.BukkitMethodHooks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class SpigotConfig {
   private static File CONFIG_FILE;
   private static final String HEADER = "This is the main configuration file for Spigot.\nAs you can see, there's tons to configure. Some options may impact gameplay, so use\nwith caution, and make sure you know what each option does before configuring.\nFor a reference for any variable inside this file, check out the Spigot wiki at\nhttp://www.spigotmc.org/wiki/spigot-configuration/\n\nIf you need help with the configuration or have any questions related to Spigot,\njoin us at the Discord or drop by our forums and leave a post.\n\nDiscord: https://www.spigotmc.org/go/discord\nForums: http://www.spigotmc.org/\n";
   public static YamlConfiguration config;
   static int version;
   static Map<String, Command> commands;
   private static Metrics metrics;
   public static boolean logCommands;
   public static int tabComplete;
   public static boolean sendNamespaced;
   public static String whitelistMessage;
   public static String unknownCommandMessage;
   public static String serverFullMessage;
   public static String outdatedClientMessage = "Outdated client! Please use {0}";
   public static String outdatedServerMessage = "Outdated server! I'm still on {0}";
   public static int timeoutTime = 60;
   public static boolean restartOnCrash = true;
   public static String restartScript = "./start.sh";
   public static String restartMessage;
   public static boolean bungee;
   public static boolean disableStatSaving;
   public static Map<ResourceLocation, Integer> forcedStats = new HashMap();
   public static int playerSample;
   public static int playerShuffle;
   public static List<String> spamExclusions;
   public static boolean silentCommandBlocks;
   public static Set<String> replaceCommands;
   public static int userCacheCap;
   public static boolean saveUserCacheOnStopOnly;
   public static double movedWronglyThreshold;
   public static double movedTooQuicklyMultiplier;
   public static double maxAbsorption = 2048.0;
   public static double maxHealth = 2048.0;
   public static double movementSpeed = 2048.0;
   public static double attackDamage = 2048.0;
   public static boolean debug;
   public static boolean disableAdvancementSaving;
   public static List<String> disabledAdvancements;
   public static boolean logVillagerDeaths;
   public static boolean logNamedDeaths;
   public static boolean disablePlayerDataSaving;
   public static boolean belowZeroGenerationInExistingChunks;

   public static void init(File configFile) {
      CONFIG_FILE = configFile;
      config = new YamlConfiguration();

      try {
         config.load(CONFIG_FILE);
      } catch (IOException var2) {
      } catch (InvalidConfigurationException var3) {
         InvalidConfigurationException ex = var3;
         Bukkit.getLogger().log(Level.SEVERE, "Could not load spigot.yml, please correct your syntax errors", ex);
         throw Throwables.propagate(ex);
      }

      config.options().header("This is the main configuration file for Spigot.\nAs you can see, there's tons to configure. Some options may impact gameplay, so use\nwith caution, and make sure you know what each option does before configuring.\nFor a reference for any variable inside this file, check out the Spigot wiki at\nhttp://www.spigotmc.org/wiki/spigot-configuration/\n\nIf you need help with the configuration or have any questions related to Spigot,\njoin us at the Discord or drop by our forums and leave a post.\n\nDiscord: https://www.spigotmc.org/go/discord\nForums: http://www.spigotmc.org/\n");
      config.options().copyDefaults(true);
      commands = new HashMap();
      commands.put("spigot", new SpigotCommand("spigot"));
      version = getInt("config-version", 12);
      set("config-version", 12);
      readConfig(SpigotConfig.class, (Object)null);
   }

   public static void registerCommands() {
      Iterator var0 = commands.entrySet().iterator();

      while(var0.hasNext()) {
         Map.Entry<String, Command> entry = (Map.Entry)var0.next();
         BukkitMethodHooks.getServer().bridge$server().getCommandMap().register((String)entry.getKey(), "Spigot", (Command)entry.getValue());
      }

      if (metrics == null) {
         try {
            metrics = new Metrics();
            metrics.start();
         } catch (IOException var2) {
            IOException ex = var2;
            Bukkit.getServer().getLogger().log(Level.SEVERE, "Could not start metrics service", ex);
         }
      }

   }

   static void readConfig(Class<?> clazz, Object instance) {
      Method[] var2 = clazz.getDeclaredMethods();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Method method = var2[var4];
         if (Modifier.isPrivate(method.getModifiers()) && method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE) {
            try {
               method.setAccessible(true);
               method.invoke(instance);
            } catch (InvocationTargetException var8) {
               throw Throwables.propagate(var8.getCause());
            } catch (Exception var9) {
               Exception ex = var9;
               Bukkit.getLogger().log(Level.SEVERE, "Error invoking " + String.valueOf(method), ex);
            }
         }
      }

      try {
         config.save(CONFIG_FILE);
      } catch (IOException var7) {
         Bukkit.getLogger().log(Level.SEVERE, "Could not save " + String.valueOf(CONFIG_FILE), var7);
      }

   }

   private static void set(String path, Object val) {
      config.set(path, val);
   }

   private static boolean getBoolean(String path, boolean def) {
      config.addDefault(path, def);
      return config.getBoolean(path, config.getBoolean(path));
   }

   private static int getInt(String path, int def) {
      config.addDefault(path, def);
      return config.getInt(path, config.getInt(path));
   }

   private static <T> List getList(String path, T def) {
      config.addDefault(path, def);
      return config.getList(path, config.getList(path));
   }

   private static String getString(String path, String def) {
      config.addDefault(path, def);
      return config.getString(path, config.getString(path));
   }

   private static double getDouble(String path, double def) {
      config.addDefault(path, def);
      return config.getDouble(path, config.getDouble(path));
   }

   private static void logCommands() {
      logCommands = getBoolean("commands.log", true);
   }

   private static void tabComplete() {
      if (version < 6) {
         boolean oldValue = getBoolean("commands.tab-complete", true);
         if (oldValue) {
            set("commands.tab-complete", 0);
         } else {
            set("commands.tab-complete", -1);
         }
      }

      tabComplete = getInt("commands.tab-complete", 0);
      sendNamespaced = getBoolean("commands.send-namespaced", true);
   }

   private static String transform(String s) {
      return ChatColor.translateAlternateColorCodes('&', s).replaceAll("\\\\n", "\n");
   }

   private static void messages() {
      if (version < 8) {
         set("messages.outdated-client", outdatedClientMessage);
         set("messages.outdated-server", outdatedServerMessage);
      }

      whitelistMessage = transform(getString("messages.whitelist", "You are not whitelisted on this server!"));
      unknownCommandMessage = transform(getString("messages.unknown-command", "Unknown command. Type \"/help\" for help."));
      serverFullMessage = transform(getString("messages.server-full", "The server is full!"));
      outdatedClientMessage = transform(getString("messages.outdated-client", outdatedClientMessage));
      outdatedServerMessage = transform(getString("messages.outdated-server", outdatedServerMessage));
   }

   private static void watchdog() {
      timeoutTime = getInt("settings.timeout-time", timeoutTime);
      restartOnCrash = getBoolean("settings.restart-on-crash", restartOnCrash);
      restartScript = getString("settings.restart-script", restartScript);
      restartMessage = transform(getString("messages.restart", "Server is restarting"));
      commands.put("restart", new RestartCommand("restart"));
      WatchdogThread.doStart(timeoutTime, restartOnCrash);
   }

   private static void bungee() {
      if (version < 4) {
         set("settings.bungeecord", false);
         System.out.println("Outdated config, disabling BungeeCord support!");
      }

      bungee = getBoolean("settings.bungeecord", false);
   }

   private static void nettyThreads() {
      int count = getInt("settings.netty-threads", 4);
      System.setProperty("io.netty.eventLoopThreads", Integer.toString(count));
      Bukkit.getLogger().log(Level.INFO, "Using {0} threads for Netty based IO", count);
   }

   private static void stats() {
      disableStatSaving = getBoolean("stats.disable-saving", false);
      if (!config.contains("stats.forced-stats")) {
         config.createSection("stats.forced-stats");
      }

      ConfigurationSection section = config.getConfigurationSection("stats.forced-stats");
      Iterator var1 = section.getKeys(true).iterator();

      while(var1.hasNext()) {
         String name = (String)var1.next();
         if (section.isInt(name)) {
            try {
               ResourceLocation key = ResourceLocation.parse(name);
               if (BuiltInRegistries.CUSTOM_STAT.get(key) == null) {
                  Bukkit.getLogger().log(Level.WARNING, "Ignoring non existent stats.forced-stats " + name);
               } else {
                  forcedStats.put(key, section.getInt(name));
               }
            } catch (Exception var4) {
               Bukkit.getLogger().log(Level.WARNING, "Ignoring invalid stats.forced-stats " + name);
            }
         }
      }

   }

   private static void tpsCommand() {
      commands.put("tps", new TicksPerSecondCommand("tps"));
   }

   private static void playerSample() {
      playerSample = getInt("settings.sample-count", 12);
      System.out.println("Server Ping Player Sample Count: " + playerSample);
   }

   private static void playerShuffle() {
      playerShuffle = getInt("settings.player-shuffle", 0);
   }

   private static void spamExclusions() {
      spamExclusions = getList("commands.spam-exclusions", Arrays.asList("/skill"));
   }

   private static void silentCommandBlocks() {
      silentCommandBlocks = getBoolean("commands.silent-commandblock-console", false);
   }

   private static void replaceCommands() {
      if (config.contains("replace-commands")) {
         set("commands.replace-commands", config.getStringList("replace-commands"));
         config.set("replace-commands", (Object)null);
      }

      replaceCommands = new HashSet(getList("commands.replace-commands", Arrays.asList("setblock", "summon", "testforblock", "tellraw")));
   }

   private static void userCacheCap() {
      userCacheCap = getInt("settings.user-cache-size", 1000);
   }

   private static void saveUserCacheOnStopOnly() {
      saveUserCacheOnStopOnly = getBoolean("settings.save-user-cache-on-stop-only", false);
   }

   private static void movedWronglyThreshold() {
      movedWronglyThreshold = getDouble("settings.moved-wrongly-threshold", 0.0625);
   }

   private static void movedTooQuicklyMultiplier() {
      movedTooQuicklyMultiplier = getDouble("settings.moved-too-quickly-multiplier", 10.0);
   }

   private static void attributeMaxes() {
      maxAbsorption = getDouble("settings.attribute.maxAbsorption.max", maxAbsorption);
      ((RangedAttribute)Attributes.MAX_ABSORPTION.value()).maxValue = maxAbsorption;
      maxHealth = getDouble("settings.attribute.maxHealth.max", maxHealth);
      ((RangedAttribute)Attributes.MAX_HEALTH.value()).maxValue = maxHealth;
      movementSpeed = getDouble("settings.attribute.movementSpeed.max", movementSpeed);
      ((RangedAttribute)Attributes.MOVEMENT_SPEED.value()).maxValue = movementSpeed;
      attackDamage = getDouble("settings.attribute.attackDamage.max", attackDamage);
      ((RangedAttribute)Attributes.ATTACK_DAMAGE.value()).maxValue = attackDamage;
   }

   private static void debug() {
      debug = getBoolean("settings.debug", false);
      if (debug && !LogManager.getRootLogger().isTraceEnabled()) {
         LoggerContext ctx = (LoggerContext)LogManager.getContext(false);
         Configuration conf = ctx.getConfiguration();
         conf.getLoggerConfig("").setLevel(org.apache.logging.log4j.Level.ALL);
         ctx.updateLoggers(conf);
      }

      if (LogManager.getRootLogger().isTraceEnabled()) {
         Bukkit.getLogger().info("Debug logging is enabled");
      } else {
         Bukkit.getLogger().info("Debug logging is disabled");
      }

   }

   private static void disabledAdvancements() {
      disableAdvancementSaving = getBoolean("advancements.disable-saving", false);
      disabledAdvancements = getList("advancements.disabled", Arrays.asList("minecraft:story/disabled"));
   }

   private static void logDeaths() {
      logVillagerDeaths = getBoolean("settings.log-villager-deaths", true);
      logNamedDeaths = getBoolean("settings.log-named-deaths", true);
   }

   private static void disablePlayerDataSaving() {
      disablePlayerDataSaving = getBoolean("players.disable-saving", false);
   }

   private static void belowZeroGenerationInExistingChunks() {
      belowZeroGenerationInExistingChunks = getBoolean("world-settings.default.below-zero-generation-in-existing-chunks", true);
   }
}
