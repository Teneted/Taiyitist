package org.bukkit.craftbukkit.command;

import java.net.SocketAddress;
import net.minecraft.network.chat.Component;
import net.minecraft.server.rcon.RconConsoleSource;
import org.bukkit.command.RemoteConsoleCommandSender;

public class CraftRemoteConsoleCommandSender extends ServerCommandSender implements RemoteConsoleCommandSender {
   private final RconConsoleSource listener;

   public CraftRemoteConsoleCommandSender(RconConsoleSource listener) {
      this.listener = listener;
   }

   public RconConsoleSource getListener() {
      return this.listener;
   }

   public SocketAddress getAddress() {
      return this.listener.socketAddress;
   }

   public void sendMessage(String message) {
      this.listener.sendSystemMessage(Component.literal(message + "\n"));
   }

   public void sendMessage(String... messages) {
      String[] var2 = messages;
      int var3 = messages.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String message = var2[var4];
         this.sendMessage(message);
      }

   }

   public String getName() {
      return "Rcon";
   }

   public boolean isOp() {
      return true;
   }

   public void setOp(boolean value) {
      throw new UnsupportedOperationException("Cannot change operator status of remote controller.");
   }
}
