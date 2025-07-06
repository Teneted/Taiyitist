package org.bukkit.craftbukkit;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class CraftCrashReport implements Supplier<String> {
   public String get() {
      StringWriter value = new StringWriter();

      try {
         value.append("\n   Running: ").append(Bukkit.getName()).append(" version ").append(Bukkit.getVersion()).append(" (Implementing API version ").append(Bukkit.getBukkitVersion()).append(") ").append(String.valueOf(MinecraftServer.getServer().usesAuthentication()));
         value.append("\n   Plugins: {");
         Plugin[] var9 = Bukkit.getPluginManager().getPlugins();
         int var11 = var9.length;

         for(int var4 = 0; var4 < var11; ++var4) {
            Plugin plugin = var9[var4];
            PluginDescriptionFile description = plugin.getDescription();
            boolean legacy = CraftMagicNumbers.isLegacy(description);
            value.append(' ').append(description.getFullName()).append(legacy ? "*" : "").append(' ').append(description.getMain()).append(' ').append(Arrays.toString(description.getAuthors().toArray())).append(',');
         }

         value.append("}\n   Warnings: ").append(Bukkit.getWarningState().name());
         value.append("\n   Reload Count: ").append(String.valueOf(MinecraftServer.getServer().server.reloadCount));
         value.append("\n   Threads: {");
         Iterator var10 = Thread.getAllStackTraces().entrySet().iterator();

         while(var10.hasNext()) {
            Map.Entry<Thread, ? extends Object[]> entry = (Map.Entry)var10.next();
            value.append(' ').append(((Thread)entry.getKey()).getState().name()).append(' ').append(((Thread)entry.getKey()).getName()).append(": ").append(Arrays.toString((Object[])entry.getValue())).append(',');
         }

         value.append("}\n   ").append(Bukkit.getScheduler().toString());
         value.append("\n   Force Loaded Chunks: {");
         var10 = Bukkit.getWorlds().iterator();

         while(var10.hasNext()) {
            World world = (World)var10.next();
            value.append(' ').append(world.getName()).append(": {");
            Iterator var14 = world.getPluginChunkTickets().entrySet().iterator();

            while(var14.hasNext()) {
               Map.Entry<Plugin, Collection<Chunk>> entry = (Map.Entry)var14.next();
               value.append(' ').append(((Plugin)entry.getKey()).getDescription().getFullName()).append(": ").append(Integer.toString(((Collection)entry.getValue()).size())).append(',');
            }

            value.append("},");
         }

         value.append("}");
      } catch (Throwable var8) {
         Throwable t = var8;
         value.append("\n   Failed to handle CraftCrashReport:\n");
         PrintWriter writer = new PrintWriter(value);
         t.printStackTrace(writer);
         writer.flush();
      }

      return value.toString();
   }
}
