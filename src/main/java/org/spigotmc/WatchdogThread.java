package org.spigotmc;

import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.teneted.taiyitist.bukkit.BukkitMethodHooks;

public class WatchdogThread extends Thread {
   private static WatchdogThread instance;
   private long timeoutTime;
   private boolean restart;
   private volatile long lastTick;
   private volatile boolean stopping;

   private WatchdogThread(long timeoutTime, boolean restart) {
      super("Spigot Watchdog Thread");
      this.timeoutTime = timeoutTime;
      this.restart = restart;
   }

   private static long monotonicMillis() {
      return System.nanoTime() / 1000000L;
   }

   public static void doStart(int timeoutTime, boolean restart) {
      if (instance == null) {
         instance = new WatchdogThread((long)timeoutTime * 1000L, restart);
         instance.start();
      } else {
         instance.timeoutTime = (long)timeoutTime * 1000L;
         instance.restart = restart;
      }

   }

   public static void tick() {
      instance.lastTick = monotonicMillis();
   }

   public static void doStop() {
      if (instance != null) {
         instance.stopping = true;
      }

   }

   public void run() {
      while(!this.stopping) {
         if (this.lastTick != 0L && this.timeoutTime > 0L && monotonicMillis() > this.lastTick + this.timeoutTime) {
            Logger log = Bukkit.getServer().getLogger();
            log.log(Level.SEVERE, "------------------------------");
            log.log(Level.SEVERE, "The server has stopped responding! This is (probably) not a Spigot bug.");
            log.log(Level.SEVERE, "If you see a plugin in the Server thread dump below, then please report it to that author");
            log.log(Level.SEVERE, "\t *Especially* if it looks like HTTP or MySQL operations are occurring");
            log.log(Level.SEVERE, "If you see a world save or edit, then it means you did far more than your server can handle at once");
            log.log(Level.SEVERE, "\t If this is the case, consider increasing timeout-time in spigot.yml but note that this will replace the crash with LARGE lag spikes");
            log.log(Level.SEVERE, "If you are unsure or still think this is a Spigot bug, please report to https://www.spigotmc.org/");
            log.log(Level.SEVERE, "Be sure to include ALL relevant console errors and Minecraft crash reports");
            log.log(Level.SEVERE, "Spigot version: " + Bukkit.getServer().getVersion());
            // Taiyitist TODO fixme
            /*
            if (net.minecraft.world.level.Level.lastPhysicsProblem != null) {
               log.log(Level.SEVERE, "------------------------------");
               log.log(Level.SEVERE, "During the run of the server, a physics stackoverflow was supressed");
               log.log(Level.SEVERE, "near " + String.valueOf(net.minecraft.world.level.Level.lastPhysicsProblem));
            }*/

            log.log(Level.SEVERE, "------------------------------");
            log.log(Level.SEVERE, "Server thread dump (Look for plugins here before reporting to Spigot!):");
            dumpThread(ManagementFactory.getThreadMXBean().getThreadInfo(BukkitMethodHooks.getServer().serverThread.getId(), Integer.MAX_VALUE), log);
            log.log(Level.SEVERE, "------------------------------");
            log.log(Level.SEVERE, "Entire Thread Dump:");
            ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
            ThreadInfo[] var3 = threads;
            int var4 = threads.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               ThreadInfo thread = var3[var5];
               dumpThread(thread, log);
            }

            log.log(Level.SEVERE, "------------------------------");
            if (this.restart && !BukkitMethodHooks.getServer().hasStopped()) {
               RestartCommand.restart();
            }
            break;
         }

         try {
            sleep(10000L);
         } catch (InterruptedException var7) {
            this.interrupt();
         }
      }

   }

   private static void dumpThread(ThreadInfo thread, Logger log) {
      log.log(Level.SEVERE, "------------------------------");
      log.log(Level.SEVERE, "Current Thread: " + thread.getThreadName());
      Level var10001 = Level.SEVERE;
      long var10002 = thread.getThreadId();
      log.log(var10001, "\tPID: " + var10002 + " | Suspended: " + thread.isSuspended() + " | Native: " + thread.isInNative() + " | State: " + String.valueOf(thread.getThreadState()));
      int var3;
      int var4;
      if (thread.getLockedMonitors().length != 0) {
         log.log(Level.SEVERE, "\tThread is waiting on monitor(s):");
         MonitorInfo[] var2 = thread.getLockedMonitors();
         var3 = var2.length;

         for(var4 = 0; var4 < var3; ++var4) {
            MonitorInfo monitor = var2[var4];
            log.log(Level.SEVERE, "\t\tLocked on:" + String.valueOf(monitor.getLockedStackFrame()));
         }
      }

      log.log(Level.SEVERE, "\tStack:");
      StackTraceElement[] var6 = thread.getStackTrace();
      var3 = var6.length;

      for(var4 = 0; var4 < var3; ++var4) {
         StackTraceElement stack = var6[var4];
         log.log(Level.SEVERE, "\t\t" + String.valueOf(stack));
      }

   }
}
