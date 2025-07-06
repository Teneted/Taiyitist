package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.ServerLinks.Entry;
import net.minecraft.server.ServerLinks.KnownLinkType;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.ServerLinks;
import org.bukkit.craftbukkit.util.CraftChatMessage;

public class CraftServerLinks implements ServerLinks {
   private final DedicatedServer server;
   private net.minecraft.server.ServerLinks serverLinks;

   public CraftServerLinks(DedicatedServer server) {
      this(server, (net.minecraft.server.ServerLinks)null);
   }

   public CraftServerLinks(net.minecraft.server.ServerLinks serverLinks) {
      this((DedicatedServer)null, serverLinks);
   }

   private CraftServerLinks(DedicatedServer server, net.minecraft.server.ServerLinks serverLinks) {
      this.server = server;
      this.serverLinks = serverLinks;
   }

   public ServerLinks.ServerLink getLink(ServerLinks.Type type) {
      Preconditions.checkArgument(type != null, "type cannot be null");
      return (ServerLinks.ServerLink)this.getServerLinks().findKnownType(fromBukkit(type)).map(CraftServerLink::new).orElse((CraftServerLink) null);
   }

   public List<ServerLinks.ServerLink> getLinks() {
      return getServerLinks().entries().stream().map(nms -> (ServerLink) new CraftServerLink(nms)).toList();
   }

   public ServerLinks.ServerLink setLink(ServerLinks.Type type, URI url) {
      Preconditions.checkArgument(type != null, "type cannot be null");
      Preconditions.checkArgument(url != null, "url cannot be null");
      ServerLinks.ServerLink existing = this.getLink(type);
      if (existing != null) {
         this.removeLink(existing);
      }

      return this.addLink(type, url);
   }

   public ServerLinks.ServerLink addLink(ServerLinks.Type type, URI url) {
      Preconditions.checkArgument(type != null, "type cannot be null");
      Preconditions.checkArgument(url != null, "url cannot be null");
      CraftServerLink link = new CraftServerLink(Entry.knownType(fromBukkit(type), url));
      this.addLink(link);
      return link;
   }

   public ServerLinks.ServerLink addLink(String displayName, URI url) {
      Preconditions.checkArgument(displayName != null, "displayName cannot be null");
      Preconditions.checkArgument(url != null, "url cannot be null");
      CraftServerLink link = new CraftServerLink(Entry.custom(CraftChatMessage.fromStringOrNull(displayName), url));
      this.addLink(link);
      return link;
   }

   private void addLink(CraftServerLink link) {
      List<net.minecraft.server.ServerLinks.Entry> lst = new ArrayList(this.getServerLinks().entries());
      lst.add(link.handle);
      this.setLinks(new net.minecraft.server.ServerLinks(lst));
   }

   public boolean removeLink(ServerLinks.ServerLink link) {
      Preconditions.checkArgument(link != null, "link cannot be null");
      List<net.minecraft.server.ServerLinks.Entry> lst = new ArrayList(this.getServerLinks().entries());
      boolean result = lst.remove(((CraftServerLink)link).handle);
      this.setLinks(new net.minecraft.server.ServerLinks(lst));
      return result;
   }

   public ServerLinks copy() {
      return new CraftServerLinks(this.getServerLinks());
   }

   public net.minecraft.server.ServerLinks getServerLinks() {
      return this.server != null ? this.server.serverLinks() : this.serverLinks;
   }

   private void setLinks(net.minecraft.server.ServerLinks links) {
      if (this.server != null) {
         this.server.serverLinks = links;
      } else {
         this.serverLinks = links;
      }

   }

   private static net.minecraft.server.ServerLinks.KnownLinkType fromBukkit(ServerLinks.Type type) {
      return KnownLinkType.values()[type.ordinal()];
   }

   private static ServerLinks.Type fromNMS(net.minecraft.server.ServerLinks.KnownLinkType nms) {
      return Type.values()[nms.ordinal()];
   }

   public static class CraftServerLink implements ServerLinks.ServerLink {
      private final net.minecraft.server.ServerLinks.Entry handle;

      public CraftServerLink(net.minecraft.server.ServerLinks.Entry handle) {
         this.handle = handle;
      }

      public ServerLinks.Type getType() {
         return (ServerLinks.Type)this.handle.type().left().map(CraftServerLinks::fromNMS).orElse((Type) null);
      }

      public String getDisplayName() {
         return CraftChatMessage.fromComponent(this.handle.displayName());
      }

      public URI getUrl() {
         return this.handle.link();
      }
   }
}
