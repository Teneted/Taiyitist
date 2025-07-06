package org.bukkit.craftbukkit.v1_21_R5.scheduler;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitWorker;

class CraftAsyncTask extends CraftTask {
   private final LinkedList<BukkitWorker> workers = new LinkedList();
   private final Map<Integer, CraftTask> runners;

   CraftAsyncTask(Map<Integer, CraftTask> runners, Plugin plugin, Object task, int id, long delay) {
      super(plugin, task, id, delay);
      this.runners = runners;
   }

   public boolean isSync() {
      return false;
   }

   public void run() {
      final Thread thread = Thread.currentThread();
      synchronized(this.workers) {
         if (this.getPeriod() == -2L) {
            return;
         }

         this.workers.add(new BukkitWorker() {
            public Thread getThread() {
               return thread;
            }

            public int getTaskId() {
               return CraftAsyncTask.this.getTaskId();
            }

            public Plugin getOwner() {
               return CraftAsyncTask.this.getOwner();
            }
         });
      }

      Throwable thrown = null;
      boolean var52 = false;

      Iterator workers;
      boolean removed;
      label762: {
         try {
            var52 = true;
            super.run();
            var52 = false;
            break label762;
         } catch (Throwable var59) {
            Throwable t = var59;
            thrown = t;
            this.getOwner().getLogger().log(Level.WARNING, String.format("Plugin %s generated an exception while executing task %s", this.getOwner().getDescription().getFullName(), this.getTaskId()), thrown);
            var52 = false;
         } finally {
            if (var52) {
               synchronized(this.workers) {
                  try {
                     Iterator<BukkitWorker> workers = this.workers.iterator();
                     boolean removed = false;

                     while(workers.hasNext()) {
                        if (((BukkitWorker)workers.next()).getThread() == thread) {
                           workers.remove();
                           removed = true;
                           break;
                        }
                     }

                     if (!removed) {
                        throw new IllegalStateException(String.format("Unable to remove worker %s on task %s for %s", thread.getName(), this.getTaskId(), this.getOwner().getDescription().getFullName()), thrown);
                     }
                  } finally {
                     if (this.getPeriod() < 0L && this.workers.isEmpty()) {
                        this.runners.remove(this.getTaskId());
                     }

                  }

               }
            }
         }

         synchronized(this.workers) {
            try {
               workers = this.workers.iterator();
               removed = false;

               while(true) {
                  if (workers.hasNext()) {
                     if (((BukkitWorker)workers.next()).getThread() != thread) {
                        continue;
                     }

                     workers.remove();
                     removed = true;
                  }

                  if (removed) {
                     return;
                  }

                  throw new IllegalStateException(String.format("Unable to remove worker %s on task %s for %s", thread.getName(), this.getTaskId(), this.getOwner().getDescription().getFullName()), thrown);
               }
            } finally {
               if (this.getPeriod() < 0L && this.workers.isEmpty()) {
                  this.runners.remove(this.getTaskId());
               }

            }
         }
      }

      synchronized(this.workers) {
         try {
            workers = this.workers.iterator();
            removed = false;

            while(workers.hasNext()) {
               if (((BukkitWorker)workers.next()).getThread() == thread) {
                  workers.remove();
                  removed = true;
                  break;
               }
            }

            if (!removed) {
               throw new IllegalStateException(String.format("Unable to remove worker %s on task %s for %s", thread.getName(), this.getTaskId(), this.getOwner().getDescription().getFullName()), thrown);
            }
         } finally {
            if (this.getPeriod() < 0L && this.workers.isEmpty()) {
               this.runners.remove(this.getTaskId());
            }

         }
      }

   }

   LinkedList<BukkitWorker> getWorkers() {
      return this.workers;
   }

   boolean cancel0() {
      synchronized(this.workers) {
         this.setPeriod(-2L);
         if (this.workers.isEmpty()) {
            this.runners.remove(this.getTaskId());
         }

         return true;
      }
   }
}
