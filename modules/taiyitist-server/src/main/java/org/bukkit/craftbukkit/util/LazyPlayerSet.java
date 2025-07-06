package org.bukkit.craftbukkit.util;

import com.google.common.base.Preconditions;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.Player;

public class LazyPlayerSet extends LazyHashSet<Player> {
   private final MinecraftServer server;

   public LazyPlayerSet(MinecraftServer server) {
      this.server = server;
   }

   HashSet<Player> makeReference() {
      Preconditions.checkState(this.reference == null, "Reference already created!");
      List<ServerPlayer> players = this.server.getPlayerList().players;
      HashSet<Player> reference = new HashSet(players.size());
      Iterator var3 = players.iterator();

      while(var3.hasNext()) {
         ServerPlayer player = (ServerPlayer)var3.next();
         reference.add(player.getBukkitEntity());
      }

      return reference;
   }
}
