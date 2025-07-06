package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.BaseEncoding;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.shorts.ShortArraySet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.dialog.Dialog;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundClearDialogPacket;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPopPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.common.ClientboundServerLinksPacket;
import net.minecraft.network.protocol.common.ClientboundShowDialogPacket;
import net.minecraft.network.protocol.common.ClientboundStoreCookiePacket;
import net.minecraft.network.protocol.common.ClientboundTransferPacket;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.network.protocol.cookie.ClientboundCookieRequestPacket;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundHurtAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderLerpSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDelayPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.UserWhiteListEntry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.bukkit.BanEntry;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Input;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Note;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.ServerLinks;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.Statistic;
import org.bukkit.WeatherType;
import org.bukkit.WorldBorder;
import org.bukkit.BanList.Type;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.ban.IpBanList;
import org.bukkit.ban.ProfileBanList;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.TileState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ManuallyAbandonedConversationCanceller;
import org.bukkit.craftbukkit.CraftEffect;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.CraftInput;
import org.bukkit.craftbukkit.CraftOfflinePlayer;
import org.bukkit.craftbukkit.CraftParticle;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftServerLinks;
import org.bukkit.craftbukkit.CraftSound;
import org.bukkit.craftbukkit.CraftStatistic;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.CraftWorldBorder;
import org.bukkit.craftbukkit.advancement.CraftAdvancement;
import org.bukkit.craftbukkit.advancement.CraftAdvancementProgress;
import org.bukkit.craftbukkit.block.CraftBlockEntityState;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.conversations.ConversationTracker;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.map.CraftMapCursor;
import org.bukkit.craftbukkit.map.CraftMapView;
import org.bukkit.craftbukkit.map.RenderData;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.potion.CraftPotionUtil;
import org.bukkit.craftbukkit.profile.CraftPlayerProfile;
import org.bukkit.craftbukkit.scoreboard.CraftScoreboard;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerHideEntityEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerShowEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerUnregisterChannelEvent;
import org.bukkit.event.player.PlayerExpCooldownChangeEvent.ChangeReason;
import org.bukkit.event.player.PlayerRespawnEvent.RespawnReason;
import org.bukkit.event.player.PlayerSpawnChangeEvent.Cause;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.AsyncCatcher;

@DelegateDeserialization(CraftOfflinePlayer.class)
public class CraftPlayer extends CraftHumanEntity implements Player {
   private long firstPlayed = 0L;
   private long lastPlayed = 0L;
   private boolean hasPlayedBefore = false;
   private final ConversationTracker conversationTracker = new ConversationTracker();
   private final Set<String> channels = new HashSet();
   private final Map<UUID, Set<WeakReference<Plugin>>> invertedVisibilityEntities = new HashMap();
   private static final WeakHashMap<Plugin, WeakReference<Plugin>> pluginWeakReferences = new WeakHashMap();
   private int hash = 0;
   private double health = 20.0;
   private boolean scaledHealth = false;
   private double healthScale = 20.0;
   private CraftWorldBorder clientWorldBorder = null;
   private BorderChangeListener clientWorldBorderListener = this.createWorldBorderListener();
   private final Queue<CookieFuture> requestedCookies = new LinkedList();
   private Component playerListHeader;
   private Component playerListFooter;
   private final Player.Spigot spigot = new Player.Spigot() {
      public InetSocketAddress getRawAddress() {
         return (InetSocketAddress)CraftPlayer.this.getHandle().connection.getRawAddress();
      }

      public void respawn() {
         if (CraftPlayer.this.getHealth() <= 0.0 && CraftPlayer.this.isOnline()) {
            CraftPlayer.this.server.getServer().getPlayerList().respawn(CraftPlayer.this.getHandle(), false, RemovalReason.KILLED, RespawnReason.PLUGIN);
         }

      }

      public Set<Player> getHiddenPlayers() {
         Set<Player> ret = new HashSet();
         Iterator var2 = CraftPlayer.this.getServer().getOnlinePlayers().iterator();

         while(var2.hasNext()) {
            Player p = (Player)var2.next();
            if (!CraftPlayer.this.canSee(p)) {
               ret.add(p);
            }
         }

         return Collections.unmodifiableSet(ret);
      }

      public void sendMessage(BaseComponent component) {
         this.sendMessage(component);
      }

      public void sendMessage(BaseComponent... components) {
         this.sendMessage(ChatMessageType.SYSTEM, components);
      }

      public void sendMessage(UUID sender, BaseComponent component) {
         this.sendMessage(ChatMessageType.CHAT, sender, component);
      }

      public void sendMessage(UUID sender, BaseComponent... components) {
         this.sendMessage(ChatMessageType.CHAT, sender, components);
      }

      public void sendMessage(ChatMessageType position, BaseComponent component) {
         this.sendMessage(position, component);
      }

      public void sendMessage(ChatMessageType position, BaseComponent... components) {
         this.sendMessage(position, (UUID)null, (BaseComponent[])components);
      }

      public void sendMessage(ChatMessageType position, UUID sender, BaseComponent component) {
         this.sendMessage(position, sender, component);
      }

      public void sendMessage(ChatMessageType position, UUID sender, BaseComponent... components) {
         if (CraftPlayer.this.getHandle().connection != null) {
            CraftPlayer.this.getHandle().connection.send(new ClientboundSystemChatPacket(components, position == ChatMessageType.ACTION_BAR));
         }
      }
   };

   public CraftPlayer(CraftServer server, ServerPlayer entity) {
      super(server, entity);
      this.firstPlayed = System.currentTimeMillis();
   }

   public GameProfile getProfile() {
      return this.getHandle().getGameProfile();
   }

   public void remove() {
      throw new UnsupportedOperationException(String.format("Cannot remove player %s, use Player#kickPlayer(String) instead.", this.getName()));
   }

   public boolean isOp() {
      return this.server.getHandle().isOp(this.getProfile());
   }

   public void setOp(boolean value) {
      if (value != this.isOp()) {
         if (value) {
            this.server.getHandle().op(this.getProfile());
         } else {
            this.server.getHandle().deop(this.getProfile());
         }

         this.perm.recalculatePermissions();
      }
   }

   public boolean isOnline() {
      return this.server.getPlayer(this.getUniqueId()) != null;
   }

   public PlayerProfile getPlayerProfile() {
      return new CraftPlayerProfile(this.getProfile());
   }

   public InetSocketAddress getAddress() {
      if (this.getHandle().connection.protocol() == null) {
         return null;
      } else {
         SocketAddress addr = this.getHandle().connection.getRemoteAddress();
         return addr instanceof InetSocketAddress ? (InetSocketAddress)addr : null;
      }
   }

   public boolean isAwaitingCookies() {
      return !this.requestedCookies.isEmpty();
   }

   public boolean handleCookieResponse(ServerboundCookieResponsePacket response) {
      CookieFuture future = (CookieFuture)this.requestedCookies.peek();
      if (future != null && future.key.equals(response.key())) {
         Preconditions.checkState(future == this.requestedCookies.poll(), "requestedCookies queue mismatch");
         future.future().complete(response.payload());
         return true;
      } else {
         return false;
      }
   }

   public boolean isTransferred() {
      return this.getHandle().transferCookieConnection.isTransferred();
   }

   public CompletableFuture<byte[]> retrieveCookie(NamespacedKey key) {
      Preconditions.checkArgument(key != null, "Cookie key cannot be null");
      CompletableFuture<byte[]> future = new CompletableFuture();
      ResourceLocation nms = CraftNamespacedKey.toMinecraft(key);
      this.requestedCookies.add(new CookieFuture(nms, future));
      this.getHandle().transferCookieConnection.sendPacket(new ClientboundCookieRequestPacket(nms));
      return future;
   }

   public void storeCookie(NamespacedKey key, byte[] value) {
      Preconditions.checkArgument(key != null, "Cookie key cannot be null");
      Preconditions.checkArgument(value != null, "Cookie value cannot be null");
      Preconditions.checkArgument(value.length <= 5120, "Cookie value too large, must be smaller than 5120 bytes");
      Preconditions.checkState(this.getHandle().transferCookieConnection.getProtocol() == ConnectionProtocol.CONFIGURATION || this.getHandle().transferCookieConnection.getProtocol() == ConnectionProtocol.PLAY, "Can only store cookie in CONFIGURATION or PLAY protocol.");
      this.getHandle().transferCookieConnection.sendPacket(new ClientboundStoreCookiePacket(CraftNamespacedKey.toMinecraft(key), value));
   }

   public void transfer(String host, int port) {
      Preconditions.checkArgument(host != null, "Host cannot be null");
      Preconditions.checkState(this.getHandle().transferCookieConnection.getProtocol() == ConnectionProtocol.CONFIGURATION || this.getHandle().transferCookieConnection.getProtocol() == ConnectionProtocol.PLAY, "Can only transfer in CONFIGURATION or PLAY protocol.");
      this.getHandle().transferCookieConnection.sendPacket(new ClientboundTransferPacket(host, port));
   }

   public double getEyeHeight(boolean ignorePose) {
      return ignorePose ? 1.62 : this.getEyeHeight();
   }

   public void sendRawMessage(String message) {
      this.sendRawMessage((UUID)null, message);
   }

   public void sendRawMessage(UUID sender, String message) {
      Preconditions.checkArgument(message != null, "message cannot be null");
      if (this.getHandle().connection != null) {
         Component[] var3 = CraftChatMessage.fromString(message);
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Component component = var3[var5];
            this.getHandle().sendSystemMessage(component);
         }

      }
   }

   public void sendMessage(String message) {
      if (!this.conversationTracker.isConversingModaly()) {
         this.sendRawMessage(message);
      }

   }

   public void sendMessage(String... messages) {
      String[] var2 = messages;
      int var3 = messages.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String message = var2[var4];
         this.sendMessage(message);
      }

   }

   public void sendMessage(UUID sender, String message) {
      if (!this.conversationTracker.isConversingModaly()) {
         this.sendRawMessage(sender, message);
      }

   }

   public void sendMessage(UUID sender, String... messages) {
      String[] var3 = messages;
      int var4 = messages.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String message = var3[var5];
         this.sendMessage(sender, message);
      }

   }

   public String getDisplayName() {
      return this.getHandle().displayName;
   }

   public void setDisplayName(String name) {
      this.getHandle().displayName = name == null ? this.getName() : name;
   }

   public String getPlayerListName() {
      return this.getHandle().listName == null ? this.getName() : CraftChatMessage.fromComponent(this.getHandle().listName);
   }

   public void setPlayerListName(String name) {
      if (name == null) {
         name = this.getName();
      }

      this.getHandle().listName = name.equals(this.getName()) ? null : CraftChatMessage.fromStringOrNull(name);
      Iterator var2 = this.server.getHandle().players.iterator();

      while(var2.hasNext()) {
         ServerPlayer player = (ServerPlayer)var2.next();
         if (player.getBukkitEntity().canSee((Player)this)) {
            player.connection.send(new ClientboundPlayerInfoUpdatePacket(Action.UPDATE_DISPLAY_NAME, this.getHandle()));
         }
      }

   }

   public int getPlayerListOrder() {
      return this.getHandle().listOrder;
   }

   public void setPlayerListOrder(int order) {
      Preconditions.checkArgument(order >= 0, "order cannot be negative");
      this.getHandle().listOrder = order;
   }

   public String getPlayerListHeader() {
      return this.playerListHeader == null ? null : CraftChatMessage.fromComponent(this.playerListHeader);
   }

   public String getPlayerListFooter() {
      return this.playerListFooter == null ? null : CraftChatMessage.fromComponent(this.playerListFooter);
   }

   public void setPlayerListHeader(String header) {
      this.playerListHeader = CraftChatMessage.fromStringOrNull(header, true);
      this.updatePlayerListHeaderFooter();
   }

   public void setPlayerListFooter(String footer) {
      this.playerListFooter = CraftChatMessage.fromStringOrNull(footer, true);
      this.updatePlayerListHeaderFooter();
   }

   public void setPlayerListHeaderFooter(String header, String footer) {
      this.playerListHeader = CraftChatMessage.fromStringOrNull(header, true);
      this.playerListFooter = CraftChatMessage.fromStringOrNull(footer, true);
      this.updatePlayerListHeaderFooter();
   }

   private void updatePlayerListHeaderFooter() {
      if (this.getHandle().connection != null) {
         ClientboundTabListPacket packet = new ClientboundTabListPacket((Component)(this.playerListHeader == null ? Component.empty() : this.playerListHeader), (Component)(this.playerListFooter == null ? Component.empty() : this.playerListFooter));
         this.getHandle().connection.send(packet);
      }
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof OfflinePlayer other)) {
         return false;
      } else if (this.getUniqueId() != null && other.getUniqueId() != null) {
         boolean uuidEquals = this.getUniqueId().equals(other.getUniqueId());
         boolean idEquals = true;
         if (other instanceof CraftPlayer) {
            idEquals = this.getEntityId() == ((CraftPlayer)other).getEntityId();
         }

         return uuidEquals && idEquals;
      } else {
         return false;
      }
   }

   public void kickPlayer(String message) {
      AsyncCatcher.catchOp("player kick");
      this.getHandle().transferCookieConnection.kickPlayer(CraftChatMessage.fromStringOrEmpty(message, true));
   }

   public void setCompassTarget(Location loc) {
      Preconditions.checkArgument(loc != null, "Location cannot be null");
      if (this.getHandle().connection != null) {
         this.getHandle().connection.send(new ClientboundSetDefaultSpawnPositionPacket(CraftLocation.toBlockPosition(loc), loc.getYaw()));
      }
   }

   public Location getCompassTarget() {
      return this.getHandle().compassTarget;
   }

   public void chat(String msg) {
      Preconditions.checkArgument(msg != null, "msg cannot be null");
      if (this.getHandle().connection != null) {
         this.getHandle().connection.chat(msg, PlayerChatMessage.system(msg), false);
      }
   }

   public boolean performCommand(String command) {
      Preconditions.checkArgument(command != null, "command cannot be null");
      return this.server.dispatchCommand(this, command);
   }

   public void playNote(Location loc, byte instrument, byte note) {
      this.playNote(loc, Instrument.getByType(instrument), new Note(note));
   }

   public void playNote(Location loc, Instrument instrument, Note note) {
      Preconditions.checkArgument(loc != null, "Location cannot be null");
      Preconditions.checkArgument(instrument != null, "Instrument cannot be null");
      Preconditions.checkArgument(note != null, "Note cannot be null");
      if (this.getHandle().connection != null) {
         Sound instrumentSound = instrument.getSound();
         if (instrumentSound != null) {
            float pitch = note.getPitch();
            this.getHandle().connection.send(new ClientboundSoundPacket(CraftSound.bukkitToMinecraftHolder(instrumentSound), SoundSource.RECORDS, (double)loc.getBlockX(), (double)loc.getBlockY(), (double)loc.getBlockZ(), 3.0F, pitch, this.getHandle().getRandom().nextLong()));
         }
      }
   }

   public void playSound(Location loc, Sound sound, float volume, float pitch) {
      this.playSound(loc, sound, SoundCategory.MASTER, volume, pitch);
   }

   public void playSound(Location loc, String sound, float volume, float pitch) {
      this.playSound(loc, sound, SoundCategory.MASTER, volume, pitch);
   }

   public void playSound(Location loc, Sound sound, SoundCategory category, float volume, float pitch) {
      this.playSound(loc, sound, category, volume, pitch, this.getHandle().random.nextLong());
   }

   public void playSound(Location loc, String sound, SoundCategory category, float volume, float pitch) {
      this.playSound(loc, sound, category, volume, pitch, this.getHandle().random.nextLong());
   }

   public void playSound(Location loc, Sound sound, SoundCategory category, float volume, float pitch, long seed) {
      if (loc != null && sound != null && category != null && this.getHandle().connection != null) {
         this.playSound0(loc, CraftSound.bukkitToMinecraftHolder(sound), SoundSource.valueOf(category.name()), volume, pitch, seed);
      }
   }

   public void playSound(Location loc, String sound, SoundCategory category, float volume, float pitch, long seed) {
      if (loc != null && sound != null && category != null && this.getHandle().connection != null) {
         this.playSound0(loc, Holder.direct(SoundEvent.createVariableRangeEvent(ResourceLocation.parse(sound))), SoundSource.valueOf(category.name()), volume, pitch, seed);
      }
   }

   private void playSound0(Location loc, Holder<SoundEvent> soundEffectHolder, SoundSource categoryNMS, float volume, float pitch, long seed) {
      Preconditions.checkArgument(loc != null, "Location cannot be null");
      if (this.getHandle().connection != null) {
         ClientboundSoundPacket packet = new ClientboundSoundPacket(soundEffectHolder, categoryNMS, loc.getX(), loc.getY(), loc.getZ(), volume, pitch, seed);
         this.getHandle().connection.send(packet);
      }
   }

   public void playSound(Entity entity, Sound sound, float volume, float pitch) {
      this.playSound(entity, sound, SoundCategory.MASTER, volume, pitch);
   }

   public void playSound(Entity entity, String sound, float volume, float pitch) {
      this.playSound(entity, sound, SoundCategory.MASTER, volume, pitch);
   }

   public void playSound(Entity entity, Sound sound, SoundCategory category, float volume, float pitch) {
      this.playSound(entity, sound, category, volume, pitch, this.getHandle().random.nextLong());
   }

   public void playSound(Entity entity, String sound, SoundCategory category, float volume, float pitch) {
      this.playSound(entity, sound, category, volume, pitch, this.getHandle().random.nextLong());
   }

   public void playSound(Entity entity, Sound sound, SoundCategory category, float volume, float pitch, long seed) {
      if (entity instanceof CraftEntity craftEntity) {
         if (sound != null && category != null && this.getHandle().connection != null) {
            this.playSound0(entity, CraftSound.bukkitToMinecraftHolder(sound), SoundSource.valueOf(category.name()), volume, pitch, seed);
            return;
         }
      }

   }

   public void playSound(Entity entity, String sound, SoundCategory category, float volume, float pitch, long seed) {
      if (entity instanceof CraftEntity craftEntity) {
         if (sound != null && category != null && this.getHandle().connection != null) {
            this.playSound0(entity, Holder.direct(SoundEvent.createVariableRangeEvent(ResourceLocation.parse(sound))), SoundSource.valueOf(category.name()), volume, pitch, seed);
            return;
         }
      }

   }

   private void playSound0(Entity entity, Holder<SoundEvent> soundEffectHolder, SoundSource categoryNMS, float volume, float pitch, long seed) {
      Preconditions.checkArgument(entity != null, "Entity cannot be null");
      Preconditions.checkArgument(soundEffectHolder != null, "Holder of SoundEffect cannot be null");
      Preconditions.checkArgument(categoryNMS != null, "SoundCategory cannot be null");
      if (this.getHandle().connection != null) {
         if (entity instanceof CraftEntity) {
            CraftEntity craftEntity = (CraftEntity)entity;
            ClientboundSoundEntityPacket packet = new ClientboundSoundEntityPacket(soundEffectHolder, categoryNMS, craftEntity.getHandle(), volume, pitch, seed);
            this.getHandle().connection.send(packet);
         }
      }
   }

   public void stopSound(Sound sound) {
      this.stopSound((Sound)sound, (SoundCategory)null);
   }

   public void stopSound(String sound) {
      this.stopSound((String)sound, (SoundCategory)null);
   }

   public void stopSound(Sound sound, SoundCategory category) {
      this.stopSound(sound.getKey().getKey(), category);
   }

   public void stopSound(String sound, SoundCategory category) {
      if (this.getHandle().connection != null) {
         this.getHandle().connection.send(new ClientboundStopSoundPacket(ResourceLocation.parse(sound), category == null ? SoundSource.MASTER : SoundSource.valueOf(category.name())));
      }
   }

   public void stopSound(SoundCategory category) {
      if (this.getHandle().connection != null) {
         this.getHandle().connection.send(new ClientboundStopSoundPacket((ResourceLocation)null, SoundSource.valueOf(category.name())));
      }
   }

   public void stopAllSounds() {
      if (this.getHandle().connection != null) {
         this.getHandle().connection.send(new ClientboundStopSoundPacket((ResourceLocation)null, (SoundSource)null));
      }
   }

   public void playEffect(Location loc, Effect effect, int data) {
      Preconditions.checkArgument(effect != null, "Effect cannot be null");
      Preconditions.checkArgument(loc != null, "Location cannot be null");
      if (this.getHandle().connection != null) {
         int packetData = effect.getId();
         ClientboundLevelEventPacket packet = new ClientboundLevelEventPacket(packetData, CraftLocation.toBlockPosition(loc), data, false);
         this.getHandle().connection.send(packet);
      }
   }

   public <T> void playEffect(Location loc, Effect effect, T data) {
      Preconditions.checkArgument(effect != null, "Effect cannot be null");
      if (data != null) {
         Preconditions.checkArgument(effect.getData() != null, "Effect.%s does not have a valid Data", effect);
         Preconditions.checkArgument(effect.getData().isAssignableFrom(data.getClass()), "%s data cannot be used for the %s effect", data.getClass().getName(), effect);
      } else {
         Preconditions.checkArgument(effect.getData() == null || effect == Effect.ELECTRIC_SPARK, "Wrong kind of data for the %s effect", effect);
      }

      int datavalue = CraftEffect.getDataValue(effect, data);
      this.playEffect(loc, effect, datavalue);
   }

   public boolean breakBlock(Block block) {
      Preconditions.checkArgument(block != null, "Block cannot be null");
      Preconditions.checkArgument(block.getWorld().equals(this.getWorld()), "Cannot break blocks across worlds");
      return this.getHandle().gameMode.destroyBlock(new BlockPos(block.getX(), block.getY(), block.getZ()));
   }

   public void sendBlockChange(Location loc, Material material, byte data) {
      if (this.getHandle().connection != null) {
         ClientboundBlockUpdatePacket packet = new ClientboundBlockUpdatePacket(CraftLocation.toBlockPosition(loc), CraftMagicNumbers.getBlock(material, data));
         this.getHandle().connection.send(packet);
      }
   }

   public void sendBlockChange(Location loc, BlockData block) {
      if (this.getHandle().connection != null) {
         ClientboundBlockUpdatePacket packet = new ClientboundBlockUpdatePacket(CraftLocation.toBlockPosition(loc), ((CraftBlockData)block).getState());
         this.getHandle().connection.send(packet);
      }
   }

   public void sendBlockChanges(Collection<BlockState> blocks) {
      Preconditions.checkArgument(blocks != null, "blocks must not be null");
      if (this.getHandle().connection != null && !blocks.isEmpty()) {
         Map<SectionPos, ChunkSectionChanges> changes = new HashMap();
         Iterator var3 = blocks.iterator();

         while(var3.hasNext()) {
            BlockState state = (BlockState)var3.next();
            CraftBlockState cstate = (CraftBlockState)state;
            BlockPos blockPosition = cstate.getPosition();
            SectionPos sectionPosition = SectionPos.of(blockPosition);
            ChunkSectionChanges sectionChanges = (ChunkSectionChanges)changes.computeIfAbsent(sectionPosition, (ignore) -> {
               return new ChunkSectionChanges();
            });
            sectionChanges.positions().add(SectionPos.sectionRelativePos(blockPosition));
            sectionChanges.blockData().add(cstate.getHandle());
         }

         var3 = changes.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry<SectionPos, ChunkSectionChanges> entry = (Map.Entry)var3.next();
            ChunkSectionChanges chunkChanges = (ChunkSectionChanges)entry.getValue();
            ClientboundSectionBlocksUpdatePacket packet = new ClientboundSectionBlocksUpdatePacket((SectionPos)entry.getKey(), chunkChanges.positions(), (net.minecraft.world.level.block.state.BlockState[])chunkChanges.blockData().toArray((x$0) -> {
               return new net.minecraft.world.level.block.state.BlockState[x$0];
            }));
            this.getHandle().connection.send(packet);
         }

      }
   }

   public void sendBlockChanges(Collection<BlockState> blocks, boolean suppressLightUpdates) {
      this.sendBlockChanges(blocks);
   }

   public void sendBlockDamage(Location loc, float progress) {
      this.sendBlockDamage(loc, progress, this.getEntityId());
   }

   public void sendBlockDamage(Location loc, float progress, Entity source) {
      Preconditions.checkArgument(source != null, "source must not be null");
      this.sendBlockDamage(loc, progress, source.getEntityId());
   }

   public void sendBlockDamage(Location loc, float progress, int sourceId) {
      Preconditions.checkArgument(loc != null, "loc must not be null");
      Preconditions.checkArgument((double)progress >= 0.0 && (double)progress <= 1.0, "progress must be between 0.0 and 1.0 (inclusive)");
      if (this.getHandle().connection != null) {
         int stage = (int)(9.0F * progress);
         if (progress == 0.0F) {
            stage = -1;
         }

         ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(sourceId, CraftLocation.toBlockPosition(loc), stage);
         this.getHandle().connection.send(packet);
      }
   }

   public void sendSignChange(Location loc, String[] lines) {
      this.sendSignChange(loc, lines, DyeColor.BLACK);
   }

   public void sendSignChange(Location loc, String[] lines, DyeColor dyeColor) {
      this.sendSignChange(loc, lines, dyeColor, false);
   }

   public void sendSignChange(Location loc, String[] lines, DyeColor dyeColor, boolean hasGlowingText) {
      Preconditions.checkArgument(loc != null, "Location cannot be null");
      Preconditions.checkArgument(dyeColor != null, "DyeColor cannot be null");
      if (lines == null) {
         lines = new String[4];
      }

      Preconditions.checkArgument(lines.length >= 4, "Must have at least 4 lines (%s)", lines.length);
      if (this.getHandle().connection != null) {
         Component[] components = CraftSign.sanitizeLines(lines);
         SignBlockEntity sign = new SignBlockEntity(CraftLocation.toBlockPosition(loc), Blocks.OAK_SIGN.defaultBlockState());
         SignText text = sign.getFrontText();
         text = text.setColor(net.minecraft.world.item.DyeColor.byId(dyeColor.getWoolData()));
         text = text.setHasGlowingText(hasGlowingText);

         for(int i = 0; i < components.length; ++i) {
            text = text.setMessage(i, components[i]);
         }

         sign.setText(text, true);
         this.getHandle().connection.send(new ClientboundBlockEntityDataPacket(sign.getBlockPos(), sign.getType(), sign.getUpdateTag(this.getHandle().registryAccess())));
      }
   }

   public void sendBlockUpdate(@NotNull Location location, @NotNull TileState tileState) throws IllegalArgumentException {
      Preconditions.checkArgument(location != null, "Location can not be null");
      Preconditions.checkArgument(tileState != null, "TileState can not be null");
      if (this.getHandle().connection != null) {
         CraftBlockEntityState<?> craftState = (CraftBlockEntityState)tileState;
         this.getHandle().connection.send(craftState.getUpdatePacket(location));
      }
   }

   public void sendEquipmentChange(LivingEntity entity, EquipmentSlot slot, ItemStack item) {
      this.sendEquipmentChange(entity, Map.of(slot, item));
   }

   public void sendEquipmentChange(LivingEntity entity, Map<EquipmentSlot, ItemStack> items) {
      Preconditions.checkArgument(entity != null, "Entity cannot be null");
      Preconditions.checkArgument(items != null, "items cannot be null");
      if (this.getHandle().connection != null) {
         List<Pair<net.minecraft.world.entity.EquipmentSlot, net.minecraft.world.item.ItemStack>> equipment = new ArrayList(items.size());
         Iterator var4 = items.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry<EquipmentSlot, ItemStack> entry = (Map.Entry)var4.next();
            EquipmentSlot slot = (EquipmentSlot)entry.getKey();
            Preconditions.checkArgument(slot != null, "Cannot set null EquipmentSlot");
            equipment.add(new Pair(CraftEquipmentSlot.getNMS(slot), CraftItemStack.asNMSCopy((ItemStack)entry.getValue())));
         }

         this.getHandle().connection.send(new ClientboundSetEquipmentPacket(entity.getEntityId(), equipment));
      }
   }

   public void sendPotionEffectChange(LivingEntity entity, PotionEffect effect) {
      Preconditions.checkArgument(entity != null, "Entity cannot be null");
      Preconditions.checkArgument(effect != null, "Effect cannot be null");
      if (this.getHandle().connection != null) {
         this.getHandle().connection.send(new ClientboundUpdateMobEffectPacket(entity.getEntityId(), CraftPotionUtil.fromBukkit(effect), true));
      }
   }

   public void sendPotionEffectChangeRemove(LivingEntity entity, PotionEffectType type) {
      Preconditions.checkArgument(entity != null, "Entity cannot be null");
      Preconditions.checkArgument(type != null, "Type cannot be null");
      if (this.getHandle().connection != null) {
         this.getHandle().connection.send(new ClientboundRemoveMobEffectPacket(entity.getEntityId(), CraftPotionEffectType.bukkitToMinecraftHolder(type)));
      }
   }

   public WorldBorder getWorldBorder() {
      return this.clientWorldBorder;
   }

   public void setWorldBorder(WorldBorder border) {
      CraftWorldBorder craftBorder = (CraftWorldBorder)border;
      if (border != null && !craftBorder.isVirtual() && !craftBorder.getWorld().equals(this.getWorld())) {
         throw new UnsupportedOperationException("Cannot set player world border to that of another world");
      } else {
         if (this.clientWorldBorder != null) {
            this.clientWorldBorder.getHandle().removeListener(this.clientWorldBorderListener);
         }

         net.minecraft.world.level.border.WorldBorder newWorldBorder;
         if (craftBorder != null && craftBorder.isVirtual()) {
            this.clientWorldBorder = craftBorder;
            this.clientWorldBorder.getHandle().addListener(this.clientWorldBorderListener);
            newWorldBorder = this.clientWorldBorder.getHandle();
         } else {
            this.clientWorldBorder = null;
            newWorldBorder = ((CraftWorldBorder)this.getWorld().getWorldBorder()).getHandle();
         }

         ServerGamePacketListenerImpl connection = this.getHandle().connection;
         connection.send(new ClientboundSetBorderSizePacket(newWorldBorder));
         connection.send(new ClientboundSetBorderLerpSizePacket(newWorldBorder));
         connection.send(new ClientboundSetBorderCenterPacket(newWorldBorder));
         connection.send(new ClientboundSetBorderWarningDelayPacket(newWorldBorder));
         connection.send(new ClientboundSetBorderWarningDistancePacket(newWorldBorder));
      }
   }

   private BorderChangeListener createWorldBorderListener() {
      return new BorderChangeListener() {
         public void onBorderSizeSet(net.minecraft.world.level.border.WorldBorder border, double size) {
            CraftPlayer.this.getHandle().connection.send(new ClientboundSetBorderSizePacket(border));
         }

         public void onBorderSizeLerping(net.minecraft.world.level.border.WorldBorder border, double size, double newSize, long time) {
            CraftPlayer.this.getHandle().connection.send(new ClientboundSetBorderLerpSizePacket(border));
         }

         public void onBorderCenterSet(net.minecraft.world.level.border.WorldBorder border, double x, double z) {
            CraftPlayer.this.getHandle().connection.send(new ClientboundSetBorderCenterPacket(border));
         }

         public void onBorderSetWarningTime(net.minecraft.world.level.border.WorldBorder border, int warningTime) {
            CraftPlayer.this.getHandle().connection.send(new ClientboundSetBorderWarningDelayPacket(border));
         }

         public void onBorderSetWarningBlocks(net.minecraft.world.level.border.WorldBorder border, int warningBlocks) {
            CraftPlayer.this.getHandle().connection.send(new ClientboundSetBorderWarningDistancePacket(border));
         }

         public void onBorderSetDamagePerBlock(net.minecraft.world.level.border.WorldBorder border, double damage) {
         }

         public void onBorderSetDamageSafeZOne(net.minecraft.world.level.border.WorldBorder border, double blocks) {
         }
      };
   }

   public boolean hasClientWorldBorder() {
      return this.clientWorldBorder != null;
   }

   public void sendMap(MapView map) {
      if (this.getHandle().connection != null) {
         RenderData data = ((CraftMapView)map).render(this);
         Collection<MapDecoration> icons = new ArrayList();
         Iterator var4 = data.cursors.iterator();

         while(var4.hasNext()) {
            MapCursor cursor = (MapCursor)var4.next();
            if (cursor.isVisible()) {
               icons.add(new MapDecoration(CraftMapCursor.CraftType.bukkitToMinecraftHolder(cursor.getType()), cursor.getX(), cursor.getY(), cursor.getDirection(), CraftChatMessage.fromStringOrOptional(cursor.getCaption())));
            }
         }

         ClientboundMapItemDataPacket packet = new ClientboundMapItemDataPacket(new MapId(map.getId()), map.getScale().getValue(), map.isLocked(), icons, new MapItemSavedData.MapPatch(0, 0, 128, 128, data.buffer));
         this.getHandle().connection.send(packet);
      }
   }

   public void sendHurtAnimation(float yaw) {
      if (this.getHandle().connection != null) {
         float actualYaw = yaw + 90.0F;
         this.getHandle().connection.send(new ClientboundHurtAnimationPacket(this.getEntityId(), actualYaw));
      }
   }

   public void sendLinks(ServerLinks links) {
      if (this.getHandle().connection != null) {
         Preconditions.checkArgument(links != null, "links cannot be null");
         net.minecraft.server.ServerLinks nms = ((CraftServerLinks)links).getServerLinks();
         this.getHandle().connection.send(new ClientboundServerLinksPacket(nms.untrust()));
      }
   }

   public void addCustomChatCompletions(Collection<String> completions) {
      this.sendCustomChatCompletionPacket(completions, net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket.Action.ADD);
   }

   public void removeCustomChatCompletions(Collection<String> completions) {
      this.sendCustomChatCompletionPacket(completions, net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket.Action.REMOVE);
   }

   public void setCustomChatCompletions(Collection<String> completions) {
      this.sendCustomChatCompletionPacket(completions, net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket.Action.SET);
   }

   private void sendCustomChatCompletionPacket(Collection<String> completions, ClientboundCustomChatCompletionsPacket.Action action) {
      if (this.getHandle().connection != null) {
         ClientboundCustomChatCompletionsPacket packet = new ClientboundCustomChatCompletionsPacket(action, new ArrayList(completions));
         this.getHandle().connection.send(packet);
      }
   }

   public void setRotation(float yaw, float pitch) {
      throw new UnsupportedOperationException("Cannot set rotation of players. Consider teleporting instead.");
   }

   public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
      Preconditions.checkArgument(location != null, "location");
      Preconditions.checkArgument(location.getWorld() != null, "location.world");
      location.checkFinite();
      ServerPlayer entity = this.getHandle();
      if (this.getHealth() != 0.0 && !entity.isRemoved()) {
         if (entity.connection == null) {
            return false;
         } else if (entity.isVehicle()) {
            return false;
         } else {
            Location from = this.getLocation();
            Location to = location;
            PlayerTeleportEvent event = new PlayerTeleportEvent(this, from, to, cause);
            this.server.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
               return false;
            } else {
               entity.stopRiding();
               if (this.isSleeping()) {
                  this.wakeup(false);
               }

               from = event.getFrom();
               to = event.getTo();
               ServerLevel fromWorld = ((CraftWorld)from.getWorld()).getHandle();
               ServerLevel toWorld = ((CraftWorld)to.getWorld()).getHandle();
               if (this.getHandle().containerMenu != this.getHandle().inventoryMenu) {
                  this.getHandle().closeContainer();
               }

               if (fromWorld == toWorld) {
                  entity.connection.teleport(to);
               } else {
                  entity.portalProcess = null;
                  this.server.getHandle().respawn(entity, true, RemovalReason.CHANGED_DIMENSION, (PlayerRespawnEvent.RespawnReason)null, to);
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   public void setSneaking(boolean sneak) {
      this.getHandle().setShiftKeyDown(sneak);
   }

   public boolean isSneaking() {
      return this.getHandle().isShiftKeyDown();
   }

   public boolean isSprinting() {
      return this.getHandle().isSprinting();
   }

   public void setSprinting(boolean sprinting) {
      this.getHandle().setSprinting(sprinting);
   }

   public void loadData() {
      this.server.getHandle().playerIo.load(this.getHandle(), ProblemReporter.DISCARDING);
   }

   public void saveData() {
      this.server.getHandle().playerIo.save(this.getHandle());
   }

   /** @deprecated */
   @Deprecated
   public void updateInventory() {
      this.getHandle().containerMenu.sendAllDataToRemote();
   }

   public void setSleepingIgnored(boolean isSleeping) {
      this.getHandle().fauxSleeping = isSleeping;
      ((CraftWorld)this.getWorld()).getHandle().updateSleepingPlayerList();
   }

   public boolean isSleepingIgnored() {
      return this.getHandle().fauxSleeping;
   }

   public Location getBedSpawnLocation() {
      return this.getRespawnLocation();
   }

   public Location getRespawnLocation() {
      ServerPlayer.RespawnConfig respawnConfig = this.getHandle().getRespawnConfig();
      if (respawnConfig == null) {
         return null;
      } else {
         ServerLevel world = this.getHandle().server.getLevel(respawnConfig.dimension());
         BlockPos bed = respawnConfig.pos();
         if (world != null && bed != null) {
            Optional<ServerPlayer.RespawnPosAngle> spawnLoc = ServerPlayer.findRespawnAndUseSpawnBlock(world, respawnConfig, true);
            if (spawnLoc.isPresent()) {
               ServerPlayer.RespawnPosAngle vec = (ServerPlayer.RespawnPosAngle)spawnLoc.get();
               return CraftLocation.toBukkit((Vec3)vec.position(), world.getWorld(), vec.yaw(), 0.0F);
            }
         }

         return null;
      }
   }

   public void setBedSpawnLocation(Location location) {
      this.setBedSpawnLocation(location, false);
   }

   public void setRespawnLocation(Location location) {
      this.setRespawnLocation(location, false);
   }

   public void setBedSpawnLocation(Location location, boolean override) {
      this.setRespawnLocation(location, override);
   }

   public void setRespawnLocation(Location location, boolean override) {
      if (location == null) {
         this.getHandle().setRespawnPosition((ServerPlayer.RespawnConfig)null, override, Cause.PLUGIN);
      } else {
         this.getHandle().setRespawnPosition(new ServerPlayer.RespawnConfig(((CraftWorld)location.getWorld()).getHandle().dimension(), CraftLocation.toBlockPosition(location), location.getYaw(), false), override, Cause.PLUGIN);
      }

   }

   public Collection<EnderPearl> getEnderPearls() {
      return (Collection)this.getHandle().getEnderPearls().stream().map((e) -> {
         return (EnderPearl)e.getBukkitEntity();
      }).collect(Collectors.toList());
   }

   public Input getCurrentInput() {
      return new CraftInput(this.getHandle().getLastClientInput());
   }

   public Location getBedLocation() {
      Optional<BlockPos> bed = this.getHandle().getSleepingPos();
      Preconditions.checkState(bed.isPresent(), "Not sleeping");
      return CraftLocation.toBukkit((BlockPos)bed.get(), this.getWorld());
   }

   public boolean hasDiscoveredRecipe(NamespacedKey recipe) {
      Preconditions.checkArgument(recipe != null, "recipe cannot be null");
      return this.getHandle().getRecipeBook().contains(CraftRecipe.toMinecraft(recipe));
   }

   public Set<NamespacedKey> getDiscoveredRecipes() {
      ImmutableSet.Builder<NamespacedKey> bukkitRecipeKeys = ImmutableSet.builder();
      this.getHandle().getRecipeBook().known.forEach((key) -> {
         bukkitRecipeKeys.add(CraftNamespacedKey.fromMinecraft(key.location()));
      });
      return bukkitRecipeKeys.build();
   }

   public void incrementStatistic(Statistic statistic) {
      CraftStatistic.incrementStatistic(this.getHandle().getStats(), statistic, this.getHandle());
   }

   public void decrementStatistic(Statistic statistic) {
      CraftStatistic.decrementStatistic(this.getHandle().getStats(), statistic, this.getHandle());
   }

   public int getStatistic(Statistic statistic) {
      return CraftStatistic.getStatistic(this.getHandle().getStats(), statistic);
   }

   public void incrementStatistic(Statistic statistic, int amount) {
      CraftStatistic.incrementStatistic(this.getHandle().getStats(), statistic, amount, this.getHandle());
   }

   public void decrementStatistic(Statistic statistic, int amount) {
      CraftStatistic.decrementStatistic(this.getHandle().getStats(), statistic, amount, this.getHandle());
   }

   public void setStatistic(Statistic statistic, int newValue) {
      CraftStatistic.setStatistic(this.getHandle().getStats(), statistic, newValue, this.getHandle());
   }

   public void incrementStatistic(Statistic statistic, Material material) {
      CraftStatistic.incrementStatistic(this.getHandle().getStats(), statistic, material, this.getHandle());
   }

   public void decrementStatistic(Statistic statistic, Material material) {
      CraftStatistic.decrementStatistic(this.getHandle().getStats(), statistic, material, this.getHandle());
   }

   public int getStatistic(Statistic statistic, Material material) {
      return CraftStatistic.getStatistic(this.getHandle().getStats(), statistic, material);
   }

   public void incrementStatistic(Statistic statistic, Material material, int amount) {
      CraftStatistic.incrementStatistic(this.getHandle().getStats(), statistic, material, amount, this.getHandle());
   }

   public void decrementStatistic(Statistic statistic, Material material, int amount) {
      CraftStatistic.decrementStatistic(this.getHandle().getStats(), statistic, material, amount, this.getHandle());
   }

   public void setStatistic(Statistic statistic, Material material, int newValue) {
      CraftStatistic.setStatistic(this.getHandle().getStats(), statistic, material, newValue, this.getHandle());
   }

   public void incrementStatistic(Statistic statistic, EntityType entityType) {
      CraftStatistic.incrementStatistic(this.getHandle().getStats(), statistic, entityType, this.getHandle());
   }

   public void decrementStatistic(Statistic statistic, EntityType entityType) {
      CraftStatistic.decrementStatistic(this.getHandle().getStats(), statistic, entityType, this.getHandle());
   }

   public int getStatistic(Statistic statistic, EntityType entityType) {
      return CraftStatistic.getStatistic(this.getHandle().getStats(), statistic, entityType);
   }

   public void incrementStatistic(Statistic statistic, EntityType entityType, int amount) {
      CraftStatistic.incrementStatistic(this.getHandle().getStats(), statistic, entityType, amount, this.getHandle());
   }

   public void decrementStatistic(Statistic statistic, EntityType entityType, int amount) {
      CraftStatistic.decrementStatistic(this.getHandle().getStats(), statistic, entityType, amount, this.getHandle());
   }

   public void setStatistic(Statistic statistic, EntityType entityType, int newValue) {
      CraftStatistic.setStatistic(this.getHandle().getStats(), statistic, entityType, newValue, this.getHandle());
   }

   public void setPlayerTime(long time, boolean relative) {
      this.getHandle().timeOffset = time;
      this.getHandle().relativeTime = relative;
   }

   public long getPlayerTimeOffset() {
      return this.getHandle().timeOffset;
   }

   public long getPlayerTime() {
      return this.getHandle().getPlayerTime();
   }

   public boolean isPlayerTimeRelative() {
      return this.getHandle().relativeTime;
   }

   public void resetPlayerTime() {
      this.setPlayerTime(0L, true);
   }

   public void setPlayerWeather(WeatherType type) {
      this.getHandle().setPlayerWeather(type, true);
   }

   public WeatherType getPlayerWeather() {
      return this.getHandle().getPlayerWeather();
   }

   public int getExpCooldown() {
      return this.getHandle().takeXpDelay;
   }

   public void setExpCooldown(int ticks) {
      this.getHandle().takeXpDelay = CraftEventFactory.callPlayerXpCooldownEvent(this.getHandle(), ticks, ChangeReason.PLUGIN).getNewCooldown();
   }

   public void resetPlayerWeather() {
      this.getHandle().resetPlayerWeather();
   }

   public boolean isBanned() {
      return ((ProfileBanList)this.server.getBanList(Type.PROFILE)).isBanned(this.getPlayerProfile());
   }

   public BanEntry<PlayerProfile> ban(String reason, Date expires, String source) {
      return this.ban(reason, expires, source, true);
   }

   public BanEntry<PlayerProfile> ban(String reason, Instant expires, String source) {
      return this.ban(reason, expires != null ? Date.from(expires) : null, source);
   }

   public BanEntry<PlayerProfile> ban(String reason, Duration duration, String source) {
      return this.ban(reason, duration != null ? Instant.now().plus(duration) : null, source);
   }

   public BanEntry<PlayerProfile> ban(String reason, Date expires, String source, boolean kickPlayer) {
      BanEntry<PlayerProfile> banEntry = ((ProfileBanList)this.server.getBanList(Type.PROFILE)).addBan(this.getPlayerProfile(), reason, expires, source);
      if (kickPlayer) {
         this.kickPlayer(reason);
      }

      return banEntry;
   }

   public BanEntry<PlayerProfile> ban(String reason, Instant instant, String source, boolean kickPlayer) {
      return this.ban(reason, instant != null ? Date.from(instant) : null, source, kickPlayer);
   }

   public BanEntry<PlayerProfile> ban(String reason, Duration duration, String source, boolean kickPlayer) {
      return this.ban(reason, duration != null ? Instant.now().plus(duration) : null, source, kickPlayer);
   }

   public BanEntry<InetAddress> banIp(String reason, Date expires, String source, boolean kickPlayer) {
      Preconditions.checkArgument(this.getAddress() != null, "The Address of this Player is null");
      BanEntry<InetAddress> banEntry = ((IpBanList)this.server.getBanList(Type.IP)).addBan(this.getAddress().getAddress(), reason, expires, source);
      if (kickPlayer) {
         this.kickPlayer(reason);
      }

      return banEntry;
   }

   public BanEntry<InetAddress> banIp(String reason, Instant instant, String source, boolean kickPlayer) {
      return this.banIp(reason, instant != null ? Date.from(instant) : null, source, kickPlayer);
   }

   public BanEntry<InetAddress> banIp(String reason, Duration duration, String source, boolean kickPlayer) {
      return this.banIp(reason, duration != null ? Instant.now().plus(duration) : null, source, kickPlayer);
   }

   public boolean isWhitelisted() {
      return this.server.getHandle().getWhiteList().isWhiteListed(this.getProfile());
   }

   public void setWhitelisted(boolean value) {
      if (value) {
         this.server.getHandle().getWhiteList().add(new UserWhiteListEntry(this.getProfile()));
      } else {
         this.server.getHandle().getWhiteList().remove(this.getProfile());
      }

   }

   public void setGameMode(GameMode mode) {
      Preconditions.checkArgument(mode != null, "GameMode cannot be null");
      if (this.getHandle().connection != null) {
         this.getHandle().setGameMode(GameType.byId(mode.getValue()));
      }
   }

   public GameMode getGameMode() {
      return GameMode.getByValue(this.getHandle().gameMode.getGameModeForPlayer().getId());
   }

   public GameMode getPreviousGameMode() {
      GameType previousGameMode = this.getHandle().gameMode.getPreviousGameModeForPlayer();
      return previousGameMode == null ? null : GameMode.getByValue(previousGameMode.getId());
   }

   public void giveExp(int exp) {
      this.getHandle().giveExperiencePoints(exp);
   }

   public void giveExpLevels(int levels) {
      this.getHandle().giveExperienceLevels(levels);
   }

   public float getExp() {
      return this.getHandle().experienceProgress;
   }

   public void setExp(float exp) {
      Preconditions.checkArgument((double)exp >= 0.0 && (double)exp <= 1.0, "Experience progress must be between 0.0 and 1.0 (%s)", exp);
      this.getHandle().experienceProgress = exp;
      this.getHandle().lastSentExp = -1;
   }

   public int getLevel() {
      return this.getHandle().experienceLevel;
   }

   public void setLevel(int level) {
      Preconditions.checkArgument(level >= 0, "Experience level must not be negative (%s)", level);
      this.getHandle().experienceLevel = level;
      this.getHandle().lastSentExp = -1;
   }

   public int getTotalExperience() {
      return this.getHandle().totalExperience;
   }

   public void setTotalExperience(int exp) {
      Preconditions.checkArgument(exp >= 0, "Total experience points must not be negative (%s)", exp);
      this.getHandle().totalExperience = exp;
   }

   public void sendExperienceChange(float progress) {
      this.sendExperienceChange(progress, this.getLevel());
   }

   public void sendExperienceChange(float progress, int level) {
      Preconditions.checkArgument((double)progress >= 0.0 && (double)progress <= 1.0, "Experience progress must be between 0.0 and 1.0 (%s)", progress);
      Preconditions.checkArgument(level >= 0, "Experience level must not be negative (%s)", level);
      if (this.getHandle().connection != null) {
         ClientboundSetExperiencePacket packet = new ClientboundSetExperiencePacket(progress, this.getTotalExperience(), level);
         this.getHandle().connection.send(packet);
      }
   }

   @Nullable
   private static WeakReference<Plugin> getPluginWeakReference(@Nullable Plugin plugin) {
      return plugin == null ? null : (WeakReference)pluginWeakReferences.computeIfAbsent(plugin, WeakReference::new);
   }

   /** @deprecated */
   @Deprecated
   public void hidePlayer(Player player) {
      this.hideEntity0((Plugin)null, player);
   }

   public void hidePlayer(Plugin plugin, Player player) {
      this.hideEntity(plugin, player);
   }

   public void hideEntity(Plugin plugin, Entity entity) {
      Preconditions.checkArgument(plugin != null, "Plugin cannot be null");
      Preconditions.checkArgument(plugin.isEnabled(), "Plugin (%s) cannot be disabled", plugin.getName());
      this.hideEntity0(plugin, entity);
   }

   private void hideEntity0(@Nullable Plugin plugin, Entity entity) {
      Preconditions.checkArgument(entity != null, "Entity hidden cannot be null");
      if (this.getHandle().connection != null) {
         if (!this.equals(entity)) {
            boolean shouldHide;
            if (entity.isVisibleByDefault()) {
               shouldHide = this.addInvertedVisibility(plugin, entity);
            } else {
               shouldHide = this.removeInvertedVisibility(plugin, entity);
            }

            if (shouldHide) {
               this.untrackAndHideEntity(entity);
            }

         }
      }
   }

   private boolean addInvertedVisibility(@Nullable Plugin plugin, Entity entity) {
      Set<WeakReference<Plugin>> invertedPlugins = (Set)this.invertedVisibilityEntities.get(entity.getUniqueId());
      if (invertedPlugins != null) {
         invertedPlugins.add(getPluginWeakReference(plugin));
         return false;
      } else {
         Set<WeakReference<Plugin>> invertedPlugins = new HashSet();
         invertedPlugins.add(getPluginWeakReference(plugin));
         this.invertedVisibilityEntities.put(entity.getUniqueId(), invertedPlugins);
         return true;
      }
   }

   private void untrackAndHideEntity(Entity entity) {
      ChunkMap tracker = this.getHandle().level().getChunkSource().chunkMap;
      net.minecraft.world.entity.Entity other = ((CraftEntity)entity).getHandle();
      ChunkMap.TrackedEntity entry = (ChunkMap.TrackedEntity)tracker.entityMap.get(other.getId());
      if (entry != null) {
         entry.removePlayer(this.getHandle());
      }

      if (other instanceof ServerPlayer otherPlayer) {
         if (otherPlayer.sentListPacket) {
            this.getHandle().connection.send(new ClientboundPlayerInfoRemovePacket(List.of(otherPlayer.getUUID())));
         }
      }

      this.server.getPluginManager().callEvent(new PlayerHideEntityEvent(this, entity));
   }

   void resetAndHideEntity(Entity entity) {
      if (!this.equals(entity)) {
         if (this.invertedVisibilityEntities.remove(entity.getUniqueId()) == null) {
            this.untrackAndHideEntity(entity);
         }

      }
   }

   /** @deprecated */
   @Deprecated
   public void showPlayer(Player player) {
      this.showEntity0((Plugin)null, player);
   }

   public void showPlayer(Plugin plugin, Player player) {
      this.showEntity(plugin, player);
   }

   public void showEntity(Plugin plugin, Entity entity) {
      Preconditions.checkArgument(plugin != null, "Plugin cannot be null");
      this.showEntity0(plugin, entity);
   }

   private void showEntity0(@Nullable Plugin plugin, Entity entity) {
      Preconditions.checkArgument(entity != null, "Entity show cannot be null");
      if (this.getHandle().connection != null) {
         if (!this.equals(entity)) {
            boolean shouldShow;
            if (entity.isVisibleByDefault()) {
               shouldShow = this.removeInvertedVisibility(plugin, entity);
            } else {
               shouldShow = this.addInvertedVisibility(plugin, entity);
            }

            if (shouldShow) {
               this.trackAndShowEntity(entity);
            }

         }
      }
   }

   private boolean removeInvertedVisibility(@Nullable Plugin plugin, Entity entity) {
      Set<WeakReference<Plugin>> invertedPlugins = (Set)this.invertedVisibilityEntities.get(entity.getUniqueId());
      if (invertedPlugins == null) {
         return false;
      } else {
         invertedPlugins.remove(getPluginWeakReference(plugin));
         if (!invertedPlugins.isEmpty()) {
            return false;
         } else {
            this.invertedVisibilityEntities.remove(entity.getUniqueId());
            return true;
         }
      }
   }

   private void trackAndShowEntity(Entity entity) {
      ChunkMap tracker = this.getHandle().level().getChunkSource().chunkMap;
      net.minecraft.world.entity.Entity other = ((CraftEntity)entity).getHandle();
      if (other instanceof ServerPlayer otherPlayer) {
         this.getHandle().connection.send(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(otherPlayer)));
      }

      ChunkMap.TrackedEntity entry = (ChunkMap.TrackedEntity)tracker.entityMap.get(other.getId());
      if (entry != null && !entry.seenBy.contains(this.getHandle().connection)) {
         entry.updatePlayer(this.getHandle());
      }

      this.server.getPluginManager().callEvent(new PlayerShowEntityEvent(this, entity));
   }

   void resetAndShowEntity(Entity entity) {
      if (!this.equals(entity)) {
         if (this.invertedVisibilityEntities.remove(entity.getUniqueId()) == null) {
            this.trackAndShowEntity(entity);
         }

      }
   }

   public void onEntityRemove(net.minecraft.world.entity.Entity entity) {
      this.invertedVisibilityEntities.remove(entity.getUUID());
   }

   public boolean canSee(Player player) {
      return this.canSee((Entity)player);
   }

   public boolean canSee(Entity entity) {
      return this.equals(entity) || entity.isVisibleByDefault() ^ this.invertedVisibilityEntities.containsKey(entity.getUniqueId());
   }

   public boolean canSeePlayer(UUID uuid) {
      Entity entity = this.getServer().getPlayer(uuid);
      return entity != null ? this.canSee((Entity)entity) : false;
   }

   public Map<String, Object> serialize() {
      Map<String, Object> result = new LinkedHashMap();
      result.put("name", this.getName());
      return result;
   }

   public Player getPlayer() {
      return this;
   }

   public ServerPlayer getHandle() {
      return (ServerPlayer)this.entity;
   }

   public void setHandle(ServerPlayer entity) {
      super.setHandle(entity);
   }

   public String toString() {
      return "CraftPlayer{name=" + this.getName() + "}";
   }

   public int hashCode() {
      if (this.hash == 0 || this.hash == 485) {
         this.hash = 485 + (this.getUniqueId() != null ? this.getUniqueId().hashCode() : 0);
      }

      return this.hash;
   }

   public long getFirstPlayed() {
      return this.firstPlayed;
   }

   public long getLastPlayed() {
      return this.lastPlayed;
   }

   public boolean hasPlayedBefore() {
      return this.hasPlayedBefore;
   }

   public void setFirstPlayed(long firstPlayed) {
      this.firstPlayed = firstPlayed;
   }

   public void readExtraData(ValueInput valueinput) {
      this.hasPlayedBefore = true;
      valueinput.child("bukkit").ifPresent((data) -> {
         this.firstPlayed = data.getLongOr("firstPlayed", this.firstPlayed);
         this.lastPlayed = data.getLongOr("lastPlayed", this.lastPlayed);
         ServerPlayer handle = this.getHandle();
         handle.newExp = data.getIntOr("newExp", handle.newExp);
         handle.newTotalExp = data.getIntOr("newTotalExp", handle.newTotalExp);
         handle.newLevel = data.getIntOr("newLevel", handle.newLevel);
         handle.expToDrop = data.getIntOr("expToDrop", handle.expToDrop);
         handle.keepLevel = data.getBooleanOr("keepLevel", handle.keepLevel);
      });
   }

   public void setExtraData(ValueOutput valueoutput) {
      ValueOutput data = valueoutput.child("bukkit");
      ServerPlayer handle = this.getHandle();
      data.putInt("newExp", handle.newExp);
      data.putInt("newTotalExp", handle.newTotalExp);
      data.putInt("newLevel", handle.newLevel);
      data.putInt("expToDrop", handle.expToDrop);
      data.putBoolean("keepLevel", handle.keepLevel);
      data.putLong("firstPlayed", this.getFirstPlayed());
      data.putLong("lastPlayed", System.currentTimeMillis());
      data.putString("lastKnownName", handle.getScoreboardName());
   }

   public boolean beginConversation(Conversation conversation) {
      return this.conversationTracker.beginConversation(conversation);
   }

   public void abandonConversation(Conversation conversation) {
      this.conversationTracker.abandonConversation(conversation, new ConversationAbandonedEvent(conversation, new ManuallyAbandonedConversationCanceller()));
   }

   public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {
      this.conversationTracker.abandonConversation(conversation, details);
   }

   public void acceptConversationInput(String input) {
      this.conversationTracker.acceptConversationInput(input);
   }

   public boolean isConversing() {
      return this.conversationTracker.isConversing();
   }

   public void sendPluginMessage(Plugin source, String channel, byte[] message) {
      StandardMessenger.validatePluginMessage(this.server.getMessenger(), source, channel, message);
      if (this.getHandle().connection != null) {
         if (this.channels.contains(channel)) {
            ResourceLocation id = ResourceLocation.parse(StandardMessenger.validateAndCorrectChannel(channel));
            this.sendCustomPayload(id, message);
         }

      }
   }

   private void sendCustomPayload(ResourceLocation id, byte[] message) {
      ClientboundCustomPayloadPacket packet = new ClientboundCustomPayloadPacket(new DiscardedPayload(id, Unpooled.wrappedBuffer(message)));
      this.getHandle().connection.send(packet);
   }

   public void setTexturePack(String url) {
      this.setResourcePack(url);
   }

   public void setResourcePack(String url) {
      this.setResourcePack(url, (byte[])null);
   }

   public void setResourcePack(String url, byte[] hash) {
      this.setResourcePack(url, hash, false);
   }

   public void setResourcePack(String url, byte[] hash, String prompt) {
      this.setResourcePack(url, hash, prompt, false);
   }

   public void setResourcePack(String url, byte[] hash, boolean force) {
      this.setResourcePack(url, hash, (String)null, force);
   }

   public void setResourcePack(String url, byte[] hash, String prompt, boolean force) {
      Preconditions.checkArgument(url != null, "Resource pack URL cannot be null");
      this.setResourcePack(UUID.nameUUIDFromBytes(url.getBytes(StandardCharsets.UTF_8)), url, hash, prompt, force);
   }

   public void setResourcePack(UUID id, String url, byte[] hash, String prompt, boolean force) {
      Preconditions.checkArgument(id != null, "Resource pack ID cannot be null");
      Preconditions.checkArgument(url != null, "Resource pack URL cannot be null");
      String hashStr = "";
      if (hash != null) {
         Preconditions.checkArgument(hash.length == 20, "Resource pack hash should be 20 bytes long but was %s", hash.length);
         hashStr = BaseEncoding.base16().lowerCase().encode(hash);
      }

      this.handlePushResourcePack(new ClientboundResourcePackPushPacket(id, url, hashStr, force, CraftChatMessage.fromStringOrOptional(prompt, true)), true);
   }

   public void addResourcePack(UUID id, String url, byte[] hash, String prompt, boolean force) {
      Preconditions.checkArgument(url != null, "Resource pack URL cannot be null");
      String hashStr = "";
      if (hash != null) {
         Preconditions.checkArgument(hash.length == 20, "Resource pack hash should be 20 bytes long but was %s", hash.length);
         hashStr = BaseEncoding.base16().lowerCase().encode(hash);
      }

      this.handlePushResourcePack(new ClientboundResourcePackPushPacket(id, url, hashStr, force, CraftChatMessage.fromStringOrOptional(prompt, true)), false);
   }

   public void removeResourcePack(UUID id) {
      Preconditions.checkArgument(id != null, "Resource pack id cannot be null");
      if (this.getHandle().connection != null) {
         this.getHandle().connection.send(new ClientboundResourcePackPopPacket(Optional.of(id)));
      }
   }

   public void removeResourcePacks() {
      if (this.getHandle().connection != null) {
         this.getHandle().connection.send(new ClientboundResourcePackPopPacket(Optional.empty()));
      }
   }

   private void handlePushResourcePack(ClientboundResourcePackPushPacket resourcePackPushPacket, boolean resetBeforePush) {
      if (this.getHandle().connection != null) {
         if (resetBeforePush) {
            this.removeResourcePacks();
         }

         this.getHandle().connection.send(resourcePackPushPacket);
      }
   }

   public void addChannel(String channel) {
      Preconditions.checkState(this.channels.size() < 128, "Cannot register channel '%s'. Too many channels registered!", channel);
      channel = StandardMessenger.validateAndCorrectChannel(channel);
      if (this.channels.add(channel)) {
         this.server.getPluginManager().callEvent(new PlayerRegisterChannelEvent(this, channel));
      }

   }

   public void removeChannel(String channel) {
      channel = StandardMessenger.validateAndCorrectChannel(channel);
      if (this.channels.remove(channel)) {
         this.server.getPluginManager().callEvent(new PlayerUnregisterChannelEvent(this, channel));
      }

   }

   public Set<String> getListeningPluginChannels() {
      return ImmutableSet.copyOf(this.channels);
   }

   public void sendSupportedChannels() {
      if (this.getHandle().connection != null) {
         Set<String> listening = this.server.getMessenger().getIncomingChannels();
         if (!listening.isEmpty()) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Iterator var3 = listening.iterator();

            while(var3.hasNext()) {
               String channel = (String)var3.next();

               try {
                  stream.write(channel.getBytes("UTF8"));
                  stream.write(0);
               } catch (IOException var6) {
                  IOException ex = var6;
                  Logger.getLogger(CraftPlayer.class.getName()).log(Level.SEVERE, "Could not send Plugin Channel REGISTER to " + this.getName(), ex);
               }
            }

            this.sendCustomPayload(ResourceLocation.withDefaultNamespace("register"), stream.toByteArray());
         }

      }
   }

   public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
      this.server.getPlayerMetadata().setMetadata(this, metadataKey, newMetadataValue);
   }

   public List<MetadataValue> getMetadata(String metadataKey) {
      return this.server.getPlayerMetadata().getMetadata(this, metadataKey);
   }

   public boolean hasMetadata(String metadataKey) {
      return this.server.getPlayerMetadata().hasMetadata(this, metadataKey);
   }

   public void removeMetadata(String metadataKey, Plugin owningPlugin) {
      this.server.getPlayerMetadata().removeMetadata(this, metadataKey, owningPlugin);
   }

   public boolean setWindowProperty(InventoryView.Property prop, int value) {
      AbstractContainerMenu container = this.getHandle().containerMenu;
      if (container.getBukkitView().getType() != prop.getType()) {
         return false;
      } else {
         container.setData(prop.getId(), value);
         return true;
      }
   }

   public void disconnect(String reason) {
      this.conversationTracker.abandonAllConversations();
      this.perm.clearPermissions();
   }

   public boolean isFlying() {
      return this.getHandle().getAbilities().flying;
   }

   public void setFlying(boolean value) {
      if (!this.getAllowFlight()) {
         Preconditions.checkArgument(!value, "Player is not allowed to fly (check #getAllowFlight())");
      }

      this.getHandle().getAbilities().flying = value;
      this.getHandle().onUpdateAbilities();
   }

   public boolean getAllowFlight() {
      return this.getHandle().getAbilities().mayfly;
   }

   public void setAllowFlight(boolean value) {
      if (this.isFlying() && !value) {
         this.getHandle().getAbilities().flying = false;
      }

      this.getHandle().getAbilities().mayfly = value;
      this.getHandle().onUpdateAbilities();
   }

   public void setFlySpeed(float value) {
      this.validateSpeed(value);
      ServerPlayer player = this.getHandle();
      player.getAbilities().flyingSpeed = value / 2.0F;
      player.onUpdateAbilities();
   }

   public void setWalkSpeed(float value) {
      this.validateSpeed(value);
      ServerPlayer player = this.getHandle();
      player.getAbilities().walkingSpeed = value / 2.0F;
      player.onUpdateAbilities();
      this.getHandle().getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double)player.getAbilities().walkingSpeed);
   }

   public float getFlySpeed() {
      return this.getHandle().getAbilities().flyingSpeed * 2.0F;
   }

   public float getWalkSpeed() {
      return this.getHandle().getAbilities().walkingSpeed * 2.0F;
   }

   private void validateSpeed(float value) {
      Preconditions.checkArgument(value <= 1.0F && value >= -1.0F, "Speed value (%s) need to be between -1f and 1f", value);
   }

   public void setMaxHealth(double amount) {
      super.setMaxHealth(amount);
      this.health = Math.min(this.health, amount);
      this.getHandle().resetSentInfo();
   }

   public void resetMaxHealth() {
      super.resetMaxHealth();
      this.getHandle().resetSentInfo();
   }

   public CraftScoreboard getScoreboard() {
      return this.server.getScoreboardManager().getPlayerBoard(this);
   }

   public void setScoreboard(Scoreboard scoreboard) {
      Preconditions.checkArgument(scoreboard != null, "Scoreboard cannot be null");
      Preconditions.checkState(this.getHandle().connection != null, "Cannot set scoreboard yet (invalid player connection)");
      this.server.getScoreboardManager().setPlayerBoard(this, scoreboard);
   }

   public void setHealthScale(double value) {
      Preconditions.checkArgument(value > 0.0, "Health value (%s) must be greater than 0", value);
      this.healthScale = value;
      this.scaledHealth = true;
      this.updateScaledHealth();
   }

   public double getHealthScale() {
      return this.healthScale;
   }

   public void setHealthScaled(boolean scale) {
      if (this.scaledHealth != (this.scaledHealth = scale)) {
         this.updateScaledHealth();
      }

   }

   public boolean isHealthScaled() {
      return this.scaledHealth;
   }

   public float getScaledHealth() {
      return (float)(this.isHealthScaled() ? this.getHealth() * this.getHealthScale() / this.getMaxHealth() : this.getHealth());
   }

   public double getHealth() {
      return this.health;
   }

   public void setRealHealth(double health) {
      this.health = health;
   }

   public void updateScaledHealth() {
      this.updateScaledHealth(true);
   }

   public void updateScaledHealth(boolean sendHealth) {
      AttributeMap attributemapserver = this.getHandle().getAttributes();
      Collection<AttributeInstance> set = attributemapserver.getSyncableAttributes();
      this.injectScaledMaxHealth(set, true);
      if (this.getHandle().connection != null) {
         this.getHandle().connection.send(new ClientboundUpdateAttributesPacket(this.getHandle().getId(), set));
         if (sendHealth) {
            this.sendHealthUpdate();
         }
      }

      this.getHandle().getEntityData().set(net.minecraft.world.entity.LivingEntity.DATA_HEALTH_ID, this.getScaledHealth());
      this.getHandle().maxHealthCache = this.getMaxHealth();
   }

   public void sendHealthUpdate(double health, int foodLevel, float saturation) {
      this.getHandle().connection.send(new ClientboundSetHealthPacket((float)health, foodLevel, saturation));
   }

   public void sendHealthUpdate() {
      FoodData foodData = this.getHandle().getFoodData();
      this.sendHealthUpdate((double)this.getScaledHealth(), foodData.getFoodLevel(), foodData.getSaturationLevel());
   }

   public void injectScaledMaxHealth(Collection<AttributeInstance> collection, boolean force) {
      if (this.scaledHealth || force) {
         Iterator var3 = collection.iterator();

         while(var3.hasNext()) {
            AttributeInstance genericInstance = (AttributeInstance)var3.next();
            if (genericInstance.getAttribute() == Attributes.MAX_HEALTH) {
               collection.remove(genericInstance);
               break;
            }
         }

         AttributeInstance dummy = new AttributeInstance(Attributes.MAX_HEALTH, (attribute) -> {
         });
         double healthMod = this.scaledHealth ? this.healthScale : this.getMaxHealth();
         if (healthMod >= 3.4028234663852886E38 || healthMod <= 0.0) {
            healthMod = 20.0;
            this.getServer().getLogger().warning(this.getName() + " tried to crash the server with a large health attribute");
         }

         dummy.setBaseValue(healthMod);
         collection.add(dummy);
      }
   }

   public Entity getSpectatorTarget() {
      net.minecraft.world.entity.Entity followed = this.getHandle().getCamera();
      return followed == this.getHandle() ? null : followed.getBukkitEntity();
   }

   public void setSpectatorTarget(Entity entity) {
      Preconditions.checkArgument(this.getGameMode() == GameMode.SPECTATOR, "Player must be in spectator mode");
      this.getHandle().setCamera(entity == null ? null : ((CraftEntity)entity).getHandle());
   }

   public void sendTitle(String title, String subtitle) {
      this.sendTitle(title, subtitle, 10, 70, 20);
   }

   public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
      ClientboundSetTitlesAnimationPacket times = new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut);
      this.getHandle().connection.send(times);
      if (title != null) {
         ClientboundSetTitleTextPacket packetTitle = new ClientboundSetTitleTextPacket(CraftChatMessage.fromString(title)[0]);
         this.getHandle().connection.send(packetTitle);
      }

      if (subtitle != null) {
         ClientboundSetSubtitleTextPacket packetSubtitle = new ClientboundSetSubtitleTextPacket(CraftChatMessage.fromString(subtitle)[0]);
         this.getHandle().connection.send(packetSubtitle);
      }

   }

   public void resetTitle() {
      ClientboundClearTitlesPacket packetReset = new ClientboundClearTitlesPacket(true);
      this.getHandle().connection.send(packetReset);
   }

   public void spawnParticle(Particle particle, Location location, int count) {
      this.spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count);
   }

   public void spawnParticle(Particle particle, double x, double y, double z, int count) {
      this.spawnParticle(particle, x, y, z, count, (Object)null);
   }

   public <T> void spawnParticle(Particle particle, Location location, int count, T data) {
      this.spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, data);
   }

   public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, T data) {
      this.spawnParticle(particle, x, y, z, count, 0.0, 0.0, 0.0, data);
   }

   public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ) {
      this.spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ);
   }

   public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ) {
      this.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, (Object)null);
   }

   public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, T data) {
      this.spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, data);
   }

   public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, T data) {
      this.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, 1.0, data);
   }

   public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra) {
      this.spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra);
   }

   public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra) {
      this.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, (Object)null);
   }

   public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
      this.spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra, data);
   }

   public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
      this.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, data, false);
   }

   public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, T data, boolean force) {
      this.spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra, data, force);
   }

   public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, T data, boolean force) {
      ClientboundLevelParticlesPacket packetplayoutworldparticles = new ClientboundLevelParticlesPacket(CraftParticle.createParticleParam(particle, data), force, false, (double)((float)x), (double)((float)y), (double)((float)z), (float)offsetX, (float)offsetY, (float)offsetZ, (float)extra, count);
      this.getHandle().connection.send(packetplayoutworldparticles);
   }

   public AdvancementProgress getAdvancementProgress(Advancement advancement) {
      Preconditions.checkArgument(advancement != null, "advancement");
      CraftAdvancement craft = (CraftAdvancement)advancement;
      PlayerAdvancements data = this.getHandle().getAdvancements();
      net.minecraft.advancements.AdvancementProgress progress = data.getOrStartProgress(craft.getHandle());
      return new CraftAdvancementProgress(craft, data, progress);
   }

   public int getClientViewDistance() {
      return this.getHandle().requestedViewDistance() == 0 ? Bukkit.getViewDistance() : this.getHandle().requestedViewDistance();
   }

   public int getPing() {
      return this.getHandle().connection.latency();
   }

   public String getLocale() {
      return this.getHandle().language;
   }

   public void updateCommands() {
      if (this.getHandle().connection != null) {
         this.getHandle().server.getCommands().sendCommands(this.getHandle());
      }
   }

   public void openBook(ItemStack book) {
      Preconditions.checkArgument(book != null, "ItemStack cannot be null");
      Preconditions.checkArgument(book.getType() == Material.WRITTEN_BOOK, "ItemStack Material (%s) must be Material.WRITTEN_BOOK", book.getType());
      ItemStack hand = this.getInventory().getItemInMainHand();
      this.getInventory().setItemInMainHand(book);
      this.getHandle().openItemGui(CraftItemStack.asNMSCopy(book), InteractionHand.MAIN_HAND);
      this.getInventory().setItemInMainHand(hand);
   }

   public void openSign(Sign sign) {
      this.openSign(sign, Side.FRONT);
   }

   public void openSign(@NotNull Sign sign, @NotNull Side side) {
      CraftSign.openSign(sign, this, side);
   }

   public void showDemoScreen() {
      if (this.getHandle().connection != null) {
         this.getHandle().connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.DEMO_EVENT, 0.0F));
      }
   }

   public boolean isAllowingServerListings() {
      return this.getHandle().allowsListing();
   }

   public void clearDialog() {
      if (this.getHandle().connection != null) {
         this.getHandle().connection.send(ClientboundClearDialogPacket.INSTANCE);
      }
   }

   public void showDialog(Dialog dialog) {
      if (this.getHandle().connection != null) {
         if (dialog != null) {
            JsonElement json = CraftChatMessage.getBungee().getDialogSerializer().toJson(dialog);
            net.minecraft.server.dialog.Dialog nms = (net.minecraft.server.dialog.Dialog)net.minecraft.server.dialog.Dialog.DIRECT_CODEC.parse(this.getHandle().level().registryAccess().createSerializationContext(JsonOps.INSTANCE), json).getOrThrow();
            this.getHandle().connection.send(new ClientboundShowDialogPacket(Holder.direct(nms)));
         }
      }
   }

   public Player.Spigot spigot() {
      return this.spigot;
   }

   public static record CookieFuture(ResourceLocation key, CompletableFuture<byte[]> future) {
      public CookieFuture(ResourceLocation key, CompletableFuture<byte[]> future) {
         this.key = key;
         this.future = future;
      }

      public ResourceLocation key() {
         return this.key;
      }

      public CompletableFuture<byte[]> future() {
         return this.future;
      }
   }

   public interface TransferCookieConnection {
      boolean isTransferred();

      ConnectionProtocol getProtocol();

      void sendPacket(Packet<?> var1);

      void kickPlayer(Component var1);
   }

   private static record ChunkSectionChanges(ShortSet positions, List<net.minecraft.world.level.block.state.BlockState> blockData) {
      public ChunkSectionChanges() {
         this(new ShortArraySet(), new ArrayList());
      }

      private ChunkSectionChanges(ShortSet positions, List<net.minecraft.world.level.block.state.BlockState> blockData) {
         this.positions = positions;
         this.blockData = blockData;
      }

      public ShortSet positions() {
         return this.positions;
      }

      public List<net.minecraft.world.level.block.state.BlockState> blockData() {
         return this.blockData;
      }
   }
}
