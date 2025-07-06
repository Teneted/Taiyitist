package org.bukkit.craftbukkit.v1_21_R5.packs;

import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftChatMessage;
import org.bukkit.packs.ResourcePack;

public class CraftResourcePack implements ResourcePack {
   private final MinecraftServer.ServerResourcePackInfo handle;

   public CraftResourcePack(MinecraftServer.ServerResourcePackInfo handle) {
      this.handle = handle;
   }

   public UUID getId() {
      return this.handle.id();
   }

   public String getUrl() {
      return this.handle.url();
   }

   public String getHash() {
      return this.handle.hash();
   }

   public String getPrompt() {
      return this.handle.prompt() == null ? "" : CraftChatMessage.fromComponent(this.handle.prompt());
   }

   public boolean isRequired() {
      return this.handle.isRequired();
   }

   public String toString() {
      String var10000 = String.valueOf(this.getId());
      return "CraftResourcePack{id=" + var10000 + ",url=" + this.getUrl() + ",hash=" + this.getHash() + ",prompt=" + this.getPrompt() + ",required=" + this.isRequired() + "}";
   }
}
