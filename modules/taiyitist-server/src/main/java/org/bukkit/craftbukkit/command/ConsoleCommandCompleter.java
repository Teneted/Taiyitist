package org.bukkit.craftbukkit.command;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import jline.console.completer.Completer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.Waitable;
import org.bukkit.event.server.TabCompleteEvent;

public class ConsoleCommandCompleter implements Completer {
   private final CraftServer server;

   public ConsoleCommandCompleter(CraftServer server) {
      this.server = server;
   }

   public int complete(final String buffer, int cursor, List<CharSequence> candidates) {
      Waitable<List<String>> waitable = new Waitable<List<String>>() {
         protected List<String> evaluate() {
            List<String> offers = ConsoleCommandCompleter.this.server.getCommandMap().tabComplete(ConsoleCommandCompleter.this.server.getConsoleSender(), buffer);
            TabCompleteEvent tabEvent = new TabCompleteEvent(ConsoleCommandCompleter.this.server.getConsoleSender(), buffer, offers == null ? Collections.EMPTY_LIST : offers);
            ConsoleCommandCompleter.this.server.getPluginManager().callEvent(tabEvent);
            return tabEvent.isCancelled() ? Collections.EMPTY_LIST : tabEvent.getCompletions();
         }
      };
      this.server.getServer().processQueue.add(waitable);

      try {
         List<String> offers = (List)waitable.get();
         if (offers == null) {
            return cursor;
         }

         candidates.addAll(offers);
         int lastSpace = buffer.lastIndexOf(32);
         if (lastSpace == -1) {
            return cursor - buffer.length();
         }

         return cursor - (buffer.length() - lastSpace - 1);
      } catch (ExecutionException var7) {
         ExecutionException e = var7;
         this.server.getLogger().log(Level.WARNING, "Unhandled exception when tab completing", e);
      } catch (InterruptedException var8) {
         Thread.currentThread().interrupt();
      }

      return cursor;
   }
}
