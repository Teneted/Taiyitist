package org.bukkit.craftbukkit;

import java.util.HashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.bukkit.craftbukkit.scheduler.CraftTask;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitTask;
import org.spigotmc.CustomTimingsHandler;

public class SpigotTimings {
   public static final CustomTimingsHandler serverTickTimer = new CustomTimingsHandler("** Full Server Tick");
   public static final CustomTimingsHandler playerListTimer = new CustomTimingsHandler("Player List");
   public static final CustomTimingsHandler commandFunctionsTimer = new CustomTimingsHandler("Command Functions");
   public static final CustomTimingsHandler connectionTimer = new CustomTimingsHandler("Connection Handler");
   public static final CustomTimingsHandler playerConnectionTimer = new CustomTimingsHandler("** PlayerConnection");
   public static final CustomTimingsHandler tickablesTimer = new CustomTimingsHandler("Tickables");
   public static final CustomTimingsHandler schedulerTimer = new CustomTimingsHandler("Scheduler");
   public static final CustomTimingsHandler timeUpdateTimer = new CustomTimingsHandler("Time Update");
   public static final CustomTimingsHandler serverCommandTimer = new CustomTimingsHandler("Server Command");
   public static final CustomTimingsHandler worldSaveTimer = new CustomTimingsHandler("World Save");
   public static final CustomTimingsHandler entityMoveTimer = new CustomTimingsHandler("** entityMove");
   public static final CustomTimingsHandler tickEntityTimer = new CustomTimingsHandler("** tickEntity");
   public static final CustomTimingsHandler activatedEntityTimer = new CustomTimingsHandler("** activatedTickEntity");
   public static final CustomTimingsHandler tickTileEntityTimer = new CustomTimingsHandler("** tickTileEntity");
   public static final CustomTimingsHandler timerEntityBaseTick = new CustomTimingsHandler("** livingEntityBaseTick");
   public static final CustomTimingsHandler timerEntityAI = new CustomTimingsHandler("** livingEntityAI");
   public static final CustomTimingsHandler timerEntityAICollision = new CustomTimingsHandler("** livingEntityAICollision");
   public static final CustomTimingsHandler timerEntityAIMove = new CustomTimingsHandler("** livingEntityAIMove");
   public static final CustomTimingsHandler timerEntityTickRest = new CustomTimingsHandler("** livingEntityTickRest");
   public static final CustomTimingsHandler processQueueTimer = new CustomTimingsHandler("processQueue");
   public static final CustomTimingsHandler schedulerSyncTimer;
   public static final CustomTimingsHandler playerCommandTimer;
   public static final CustomTimingsHandler entityActivationCheckTimer;
   public static final CustomTimingsHandler checkIfActiveTimer;
   public static final HashMap<String, CustomTimingsHandler> entityTypeTimingMap;
   public static final HashMap<String, CustomTimingsHandler> tileEntityTypeTimingMap;
   public static final HashMap<String, CustomTimingsHandler> pluginTaskTimingMap;

   public static CustomTimingsHandler getPluginTaskTimings(BukkitTask task, long period) {
      if (!task.isSync()) {
         return null;
      } else {
         CraftTask ctask = (CraftTask)task;
         String plugin;
         if (task.getOwner() != null) {
            plugin = task.getOwner().getDescription().getFullName();
         } else {
            plugin = "Unknown";
         }

         String taskname = ctask.getTaskName();
         String name = "Task: " + plugin + " Runnable: " + taskname;
         if (period > 0L) {
            name = name + "(interval:" + period + ")";
         } else {
            name = name + "(Single)";
         }

         CustomTimingsHandler result = (CustomTimingsHandler)pluginTaskTimingMap.get(name);
         if (result == null) {
            result = new CustomTimingsHandler(name, schedulerSyncTimer);
            pluginTaskTimingMap.put(name, result);
         }

         return result;
      }
   }

   public static CustomTimingsHandler getEntityTimings(Entity entity) {
      String entityType = entity.getClass().getName();
      CustomTimingsHandler result = (CustomTimingsHandler)entityTypeTimingMap.get(entityType);
      if (result == null) {
         result = new CustomTimingsHandler("** tickEntity - " + entity.getClass().getSimpleName(), activatedEntityTimer);
         entityTypeTimingMap.put(entityType, result);
      }

      return result;
   }

   public static CustomTimingsHandler getTileEntityTimings(BlockEntity entity) {
      String entityType = entity.getClass().getName();
      CustomTimingsHandler result = (CustomTimingsHandler)tileEntityTypeTimingMap.get(entityType);
      if (result == null) {
         result = new CustomTimingsHandler("** tickTileEntity - " + entity.getClass().getSimpleName(), tickTileEntityTimer);
         tileEntityTypeTimingMap.put(entityType, result);
      }

      return result;
   }

   static {
      schedulerSyncTimer = new CustomTimingsHandler("** Scheduler - Sync Tasks", JavaPluginLoader.pluginParentTimer);
      playerCommandTimer = new CustomTimingsHandler("** playerCommand");
      entityActivationCheckTimer = new CustomTimingsHandler("entityActivationCheck");
      checkIfActiveTimer = new CustomTimingsHandler("** checkIfActive");
      entityTypeTimingMap = new HashMap();
      tileEntityTypeTimingMap = new HashMap();
      pluginTaskTimingMap = new HashMap();
   }

   public static class WorldTimingsHandler {
      public final CustomTimingsHandler mobSpawn;
      public final CustomTimingsHandler doChunkUnload;
      public final CustomTimingsHandler doTickPending;
      public final CustomTimingsHandler doTickTiles;
      public final CustomTimingsHandler doChunkMap;
      public final CustomTimingsHandler doSounds;
      public final CustomTimingsHandler entityTick;
      public final CustomTimingsHandler tileEntityTick;
      public final CustomTimingsHandler tileEntityPending;
      public final CustomTimingsHandler tracker;
      public final CustomTimingsHandler doTick;
      public final CustomTimingsHandler tickEntities;
      public final CustomTimingsHandler syncChunkLoadTimer;
      public final CustomTimingsHandler syncChunkLoadStructuresTimer;
      public final CustomTimingsHandler syncChunkLoadEntitiesTimer;
      public final CustomTimingsHandler syncChunkLoadTileEntitiesTimer;
      public final CustomTimingsHandler syncChunkLoadTileTicksTimer;
      public final CustomTimingsHandler syncChunkLoadPostTimer;

      public WorldTimingsHandler(Level server) {
         String name = ((PrimaryLevelData)server.levelData).getLevelName() + " - ";
         this.mobSpawn = new CustomTimingsHandler("** " + name + "mobSpawn");
         this.doChunkUnload = new CustomTimingsHandler("** " + name + "doChunkUnload");
         this.doTickPending = new CustomTimingsHandler("** " + name + "doTickPending");
         this.doTickTiles = new CustomTimingsHandler("** " + name + "doTickTiles");
         this.doChunkMap = new CustomTimingsHandler("** " + name + "doChunkMap");
         this.doSounds = new CustomTimingsHandler("** " + name + "doSounds");
         this.entityTick = new CustomTimingsHandler("** " + name + "entityTick");
         this.tileEntityTick = new CustomTimingsHandler("** " + name + "tileEntityTick");
         this.tileEntityPending = new CustomTimingsHandler("** " + name + "tileEntityPending");
         this.syncChunkLoadTimer = new CustomTimingsHandler("** " + name + "syncChunkLoad");
         this.syncChunkLoadStructuresTimer = new CustomTimingsHandler("** " + name + "chunkLoad - Structures");
         this.syncChunkLoadEntitiesTimer = new CustomTimingsHandler("** " + name + "chunkLoad - Entities");
         this.syncChunkLoadTileEntitiesTimer = new CustomTimingsHandler("** " + name + "chunkLoad - TileEntities");
         this.syncChunkLoadTileTicksTimer = new CustomTimingsHandler("** " + name + "chunkLoad - TileTicks");
         this.syncChunkLoadPostTimer = new CustomTimingsHandler("** " + name + "chunkLoad - Post");
         this.tracker = new CustomTimingsHandler(name + "tracker");
         this.doTick = new CustomTimingsHandler(name + "doTick");
         this.tickEntities = new CustomTimingsHandler(name + "tickEntities");
      }
   }
}
