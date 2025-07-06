package org.bukkit.craftbukkit.boss;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;

public class CraftBossBar implements BossBar {
   private final ServerBossEvent handle;
   private Map<BarFlag, FlagContainer> flags;

   public CraftBossBar(String title, BarColor color, BarStyle style, BarFlag... flags) {
      this.handle = new ServerBossEvent(CraftChatMessage.fromString(title, true)[0], this.convertColor(color), this.convertStyle(style));
      this.initialize();
      BarFlag[] var5 = flags;
      int var6 = flags.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         BarFlag flag = var5[var7];
         this.addFlag(flag);
      }

      this.setColor(color);
      this.setStyle(style);
   }

   public CraftBossBar(ServerBossEvent bossBattleServer) {
      this.handle = bossBattleServer;
      this.initialize();
   }

   private void initialize() {
      this.flags = new HashMap();
      Map var10000 = this.flags;
      BarFlag var10001 = BarFlag.DARKEN_SKY;
      ServerBossEvent var10005 = this.handle;
      Objects.requireNonNull(var10005);
      Supplier var1 = var10005::shouldDarkenScreen;
      ServerBossEvent var10006 = this.handle;
      Objects.requireNonNull(var10006);
      var10000.put(var10001, new FlagContainer(this, var1, var10006::setDarkenScreen));
      var10000 = this.flags;
      var10001 = BarFlag.PLAY_BOSS_MUSIC;
      var10005 = this.handle;
      Objects.requireNonNull(var10005);
      var1 = var10005::shouldPlayBossMusic;
      var10006 = this.handle;
      Objects.requireNonNull(var10006);
      var10000.put(var10001, new FlagContainer(this, var1, var10006::setPlayBossMusic));
      var10000 = this.flags;
      var10001 = BarFlag.CREATE_FOG;
      var10005 = this.handle;
      Objects.requireNonNull(var10005);
      var1 = var10005::shouldCreateWorldFog;
      var10006 = this.handle;
      Objects.requireNonNull(var10006);
      var10000.put(var10001, new FlagContainer(this, var1, var10006::setCreateWorldFog));
   }

   private BarColor convertColor(BossEvent.BossBarColor color) {
      BarColor bukkitColor = BarColor.valueOf(color.name());
      return bukkitColor == null ? BarColor.WHITE : bukkitColor;
   }

   private BossEvent.BossBarColor convertColor(BarColor color) {
      BossEvent.BossBarColor nmsColor = BossBarColor.valueOf(color.name());
      return nmsColor == null ? BossBarColor.WHITE : nmsColor;
   }

   private BossEvent.BossBarOverlay convertStyle(BarStyle style) {
      switch (style) {
         case SOLID:
         default:
            return BossBarOverlay.PROGRESS;
         case SEGMENTED_6:
            return BossBarOverlay.NOTCHED_6;
         case SEGMENTED_10:
            return BossBarOverlay.NOTCHED_10;
         case SEGMENTED_12:
            return BossBarOverlay.NOTCHED_12;
         case SEGMENTED_20:
            return BossBarOverlay.NOTCHED_20;
      }
   }

   private BarStyle convertStyle(BossEvent.BossBarOverlay style) {
      switch (style) {
         case PROGRESS:
         default:
            return BarStyle.SOLID;
         case NOTCHED_6:
            return BarStyle.SEGMENTED_6;
         case NOTCHED_10:
            return BarStyle.SEGMENTED_10;
         case NOTCHED_12:
            return BarStyle.SEGMENTED_12;
         case NOTCHED_20:
            return BarStyle.SEGMENTED_20;
      }
   }

   public String getTitle() {
      return CraftChatMessage.fromComponent(this.handle.name);
   }

   public void setTitle(String title) {
      this.handle.name = CraftChatMessage.fromString(title, true)[0];
      this.handle.broadcast(ClientboundBossEventPacket::createUpdateNamePacket);
   }

   public BarColor getColor() {
      return this.convertColor(this.handle.color);
   }

   public void setColor(BarColor color) {
      this.handle.color = this.convertColor(color);
      this.handle.broadcast(ClientboundBossEventPacket::createUpdateStylePacket);
   }

   public BarStyle getStyle() {
      return this.convertStyle(this.handle.overlay);
   }

   public void setStyle(BarStyle style) {
      this.handle.overlay = this.convertStyle(style);
      this.handle.broadcast(ClientboundBossEventPacket::createUpdateStylePacket);
   }

   public void addFlag(BarFlag flag) {
      FlagContainer flagContainer = (FlagContainer)this.flags.get(flag);
      if (flagContainer != null) {
         flagContainer.set.accept(true);
      }

   }

   public void removeFlag(BarFlag flag) {
      FlagContainer flagContainer = (FlagContainer)this.flags.get(flag);
      if (flagContainer != null) {
         flagContainer.set.accept(false);
      }

   }

   public boolean hasFlag(BarFlag flag) {
      FlagContainer flagContainer = (FlagContainer)this.flags.get(flag);
      return flagContainer != null ? (Boolean)flagContainer.get.get() : false;
   }

   public void setProgress(double progress) {
      Preconditions.checkArgument(progress >= 0.0 && progress <= 1.0, "Progress must be between 0.0 and 1.0 (%s)", progress);
      this.handle.setProgress((float)progress);
   }

   public double getProgress() {
      return (double)this.handle.getProgress();
   }

   public void addPlayer(Player player) {
      Preconditions.checkArgument(player != null, "player == null");
      Preconditions.checkArgument(((CraftPlayer)player).getHandle().connection != null, "player is not fully connected (wait for PlayerJoinEvent)");
      this.handle.addPlayer(((CraftPlayer)player).getHandle());
   }

   public void removePlayer(Player player) {
      Preconditions.checkArgument(player != null, "player == null");
      this.handle.removePlayer(((CraftPlayer)player).getHandle());
   }

   public List<Player> getPlayers() {
      ImmutableList.Builder<Player> players = ImmutableList.builder();
      Iterator var2 = this.handle.getPlayers().iterator();

      while(var2.hasNext()) {
         ServerPlayer p = (ServerPlayer)var2.next();
         players.add(p.getBukkitEntity());
      }

      return players.build();
   }

   public void setVisible(boolean visible) {
      this.handle.setVisible(visible);
   }

   public boolean isVisible() {
      return this.handle.visible;
   }

   public void show() {
      this.handle.setVisible(true);
   }

   public void hide() {
      this.handle.setVisible(false);
   }

   public void removeAll() {
      Iterator var1 = this.getPlayers().iterator();

      while(var1.hasNext()) {
         Player player = (Player)var1.next();
         this.removePlayer(player);
      }

   }

   public ServerBossEvent getHandle() {
      return this.handle;
   }

   private final class FlagContainer {
      private Supplier<Boolean> get;
      private Consumer<Boolean> set;

      private FlagContainer(final CraftBossBar var1, Supplier get, Consumer set) {
         this.get = get;
         this.set = set;
      }
   }
}
