package org.bukkit.craftbukkit.ban;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.InetAddresses;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import net.minecraft.server.players.IpBanListEntry;
import org.bukkit.BanEntry;
import org.bukkit.ban.IpBanList;

public class CraftIpBanList implements IpBanList {
   private final net.minecraft.server.players.IpBanList list;

   public CraftIpBanList(net.minecraft.server.players.IpBanList list) {
      this.list = list;
   }

   public BanEntry<InetAddress> getBanEntry(String target) {
      Preconditions.checkArgument(target != null, "Target cannot be null");
      IpBanListEntry entry = (IpBanListEntry)this.list.get(target);
      return entry == null ? null : new CraftIpBanEntry(target, entry, this.list);
   }

   public BanEntry<InetAddress> getBanEntry(InetAddress target) {
      return this.getBanEntry(this.getIpFromAddress(target));
   }

   public BanEntry<InetAddress> addBan(String target, String reason, Date expires, String source) {
      Preconditions.checkArgument(target != null, "Ban target cannot be null");
      IpBanListEntry entry = new IpBanListEntry(target, new Date(), source != null && !source.isBlank() ? source : null, expires, reason != null && !reason.isBlank() ? reason : null);
      this.list.add(entry);
      return new CraftIpBanEntry(target, entry, this.list);
   }

   public BanEntry<InetAddress> addBan(InetAddress target, String reason, Date expires, String source) {
      return this.addBan(this.getIpFromAddress(target), reason, expires, source);
   }

   public BanEntry<InetAddress> addBan(InetAddress target, String reason, Instant expires, String source) {
      Date date = expires != null ? Date.from(expires) : null;
      return this.addBan(target, reason, date, source);
   }

   public BanEntry<InetAddress> addBan(InetAddress target, String reason, Duration duration, String source) {
      Instant instant = duration != null ? Instant.now().plus(duration) : null;
      return this.addBan(target, reason, instant, source);
   }

   public Set<BanEntry> getBanEntries() {
      ImmutableSet.Builder<BanEntry> builder = ImmutableSet.builder();
      String[] var2 = this.list.getUserList();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String target = var2[var4];
         IpBanListEntry ipBanEntry = (IpBanListEntry)this.list.get(target);
         if (ipBanEntry != null) {
            builder.add(new CraftIpBanEntry(target, ipBanEntry, this.list));
         }
      }

      return builder.build();
   }

   public Set<BanEntry<InetAddress>> getEntries() {
      ImmutableSet.Builder<BanEntry<InetAddress>> builder = ImmutableSet.builder();
      String[] var2 = this.list.getUserList();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String target = var2[var4];
         IpBanListEntry ipBanEntry = (IpBanListEntry)this.list.get(target);
         if (ipBanEntry != null) {
            builder.add(new CraftIpBanEntry(target, ipBanEntry, this.list));
         }
      }

      return builder.build();
   }

   public boolean isBanned(String target) {
      Preconditions.checkArgument(target != null, "Target cannot be null");
      return this.list.isBanned(target);
   }

   public boolean isBanned(InetAddress target) {
      return this.isBanned(this.getIpFromAddress(target));
   }

   public void pardon(String target) {
      Preconditions.checkArgument(target != null, "Target cannot be null");
      this.list.remove(target);
   }

   public void pardon(InetAddress target) {
      this.pardon(this.getIpFromAddress(target));
   }

   private String getIpFromAddress(InetAddress address) {
      return address == null ? null : InetAddresses.toAddrString(address);
   }
}
