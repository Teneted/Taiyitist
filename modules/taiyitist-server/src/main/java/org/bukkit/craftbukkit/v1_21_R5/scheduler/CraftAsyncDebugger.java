package org.bukkit.craftbukkit.v1_21_R5.scheduler;

import org.bukkit.plugin.Plugin;

class CraftAsyncDebugger {
   private CraftAsyncDebugger next = null;
   private final int expiry;
   private final Plugin plugin;
   private final Class<?> clazz;

   CraftAsyncDebugger(int expiry, Plugin plugin, Class<?> clazz) {
      this.expiry = expiry;
      this.plugin = plugin;
      this.clazz = clazz;
   }

   final CraftAsyncDebugger getNextHead(int time) {
      CraftAsyncDebugger next;
      CraftAsyncDebugger current;
      for(current = this; time > current.expiry && (next = current.next) != null; current = next) {
      }

      return current;
   }

   final CraftAsyncDebugger setNext(CraftAsyncDebugger next) {
      return this.next = next;
   }

   StringBuilder debugTo(StringBuilder string) {
      for(CraftAsyncDebugger next = this; next != null; next = next.next) {
         string.append(next.plugin.getDescription().getName()).append(':').append(next.clazz.getName()).append('@').append(next.expiry).append(',');
      }

      return string;
   }
}
