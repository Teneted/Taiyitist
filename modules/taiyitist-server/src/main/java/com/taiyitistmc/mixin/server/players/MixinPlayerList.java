package com.taiyitistmc.mixin.server.players;

import com.google.common.collect.Sets;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.authlib.GameProfile;
import com.taiyitistmc.TaiyitistMod;
import com.taiyitistmc.fabric.BukkitRegistry;
import com.taiyitistmc.injection.server.players.InjectionPlayerList;
import com.taiyitistmc.util.I18n;

import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.ChatFormatting;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.server.players.IpBanList;
import net.minecraft.server.players.IpBanListEntry;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.ValueInput;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList implements InjectionPlayerList {

    public AtomicReference<ServerLoginPacketListenerImpl> handler = new AtomicReference<>(null);
    public ServerLevel taiyitist$worldserver = null;
    public AtomicBoolean avoidSuffocation = new AtomicBoolean(true);

    @Shadow public abstract void broadcastSystemMessage(Component component, boolean bl);

    @Mutable
    @Shadow @Final private List<ServerPlayer> players;
    @Shadow @Final private MinecraftServer server;
    @Shadow @Final private static Logger LOGGER;
    @Shadow @Final private UserBanList bans;

    @Shadow public abstract boolean isWhiteListed(GameProfile gameProfile);

    @Shadow @Final private IpBanList ipBans;
    @Shadow @Final protected int maxPlayers;

    @Shadow public abstract boolean canBypassPlayerLimit(GameProfile gameProfile);

    @Shadow @Final private static SimpleDateFormat BAN_DATE_FORMAT;

    @Shadow protected abstract void save(ServerPlayer serverPlayer);

    private CraftServer cserver;
    private final AtomicReference<ServerPlayer> entity = new AtomicReference<>(null);
    private Location taiyitist$loc = null;
    private transient PlayerRespawnEvent.RespawnReason taiyitist$respawnReason;
    private final AtomicReference<ServerPlayer> taiyitist$worldBorderPlayer = new AtomicReference<>();

    @Inject(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/server/players/PlayerList;bans:Lnet/minecraft/server/players/UserBanList;"))
    public void taiyitist$init(MinecraftServer minecraftServer, LayeredRegistryAccess<RegistryLayer> layeredRegistryAccess, PlayerDataStorage playerDataStorage, int i, CallbackInfo ci) {
        this.players = new CopyOnWriteArrayList<>();
        minecraftServer.taiyitist$setServer(this.cserver =
                new CraftServer((DedicatedServer) minecraftServer, ((PlayerList) (Object) this)));
        TaiyitistMod.LOGGER.info(I18n.as("registry.begin"));
        BukkitRegistry.registerAll((DedicatedServer) minecraftServer);
        minecraftServer.taiyitist$setConsole(ColouredConsoleSender.getInstance());
    }

    @Redirect(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getLevel(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/server/level/ServerLevel;"))
    private ServerLevel taiyitist$spawnLocationEvent(MinecraftServer minecraftServer, ResourceKey<Level> dimension, Connection netManager, ServerPlayer playerIn) {
        CraftPlayer player = playerIn.getBukkitEntity();
        PlayerSpawnLocationEvent event = new PlayerSpawnLocationEvent(player, player.getLocation());
        cserver.getPluginManager().callEvent(event);
        Location loc = event.getSpawnLocation();
        ServerLevel world = ((CraftWorld) loc.getWorld()).getHandle();
        playerIn.setServerLevel(world);
        playerIn.snapTo(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        return world;
    }

    @Redirect(method = "placeNewPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/server/players/PlayerList;viewDistance:I"))
    private int taiyitist$spigotViewDistance(PlayerList playerList, Connection netManager, ServerPlayer playerIn) {
        return playerIn.level().bridge$spigotConfig().viewDistance;
    }

    @Redirect(method = "placeNewPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/server/players/PlayerList;simulationDistance:I"))
    private int taiyitist$spigotSimDistance(PlayerList instance, Connection netManager, ServerPlayer playerIn) {
        return playerIn.level().bridge$spigotConfig().simulationDistance;
    }

    /*
    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/server/players/PlayerList;stats:Ljava/util/Map;"))
    private void taiyitist$cancelStats(PlayerList instance, Map<UUID, ServerStatsCounter> value) {
    }

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/server/players/PlayerList;advancements:Ljava/util/Map;"))
    private void taiyitist$cancelAdvancements(PlayerList instance, Map<UUID, ServerStatsCounter> value) {
    }*/

    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE",
            target = "Ljava/util/Optional;flatMap(Ljava/util/function/Function;)Ljava/util/Optional;"))
    public void taiyitist$print(Connection connection, ServerPlayer serverPlayer, CommonListenerCookie commonListenerCookie, CallbackInfo ci, @Local String string, @Local Optional<ValueInput> optional) {
        // CraftBukkit start - Better rename detection
        if (optional.isPresent()) {
            ValueInput valueinput = optional.get();
            ValueInput bukkit = valueinput.childOrEmpty("bukkit");
            string = bukkit.getStringOr("lastKnownName", string);
        }
        // CraftBukkit end
    }

    @Redirect(method = "placeNewPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;OVERWORLD:Lnet/minecraft/resources/ResourceKey;"))
    private ResourceKey<Level> taiyitist$addDimension(@Local(argsOnly = true) ServerPlayer serverPlayer) {
        return serverPlayer.level().dimension(); // CraftBukkit - SPIGOT-7507: If no dimension, fall back to existing dimension loaded from "WorldUUID", which in turn defaults to World.OVERWORLD
    }

    @Redirect(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;[Ljava/lang/Object;)V"))
    private void taiyitist$moveLogger(Logger instance, String s, Object[] objects) {
    }

    @Inject(method = "placeNewPlayer", at = @At(value = "NEW", target = "(Lnet/minecraft/world/Difficulty;Z)Lnet/minecraft/network/protocol/game/ClientboundChangeDifficultyPacket;"))
    private void taiyitist$sendSupportedChannels(Connection connection, ServerPlayer serverPlayer, CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
        serverPlayer.getBukkitEntity().sendSupportedChannels(); // CraftBukkit
    }

    @Redirect(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    private void taiyitist$resetMsg(PlayerList instance, Component component, boolean bl, @Local MutableComponent mutableComponent, @Share("taiyitist$joinMsg") LocalRef<String> taiyitist$joinMsg) {
        // CraftBukkit start
        mutableComponent.withStyle(ChatFormatting.YELLOW);
        String joinMessage = CraftChatMessage.fromComponent(mutableComponent);
        taiyitist$joinMsg.set(joinMessage);
    }

    @Redirect(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 5))
    private void taiyitist$cancelSendPacket(ServerGamePacketListenerImpl instance, Packet packet) {
    }

    @Redirect(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void taiyitist$cancelBroadcastMsg(PlayerList instance, Packet<?> packet) {
    }

    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;sendLevelInfo(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/server/level/ServerLevel;)V"))
    private void taiyitist$handlePlayerJoinEvent(Connection connection, ServerPlayer entityplayer, CommonListenerCookie commonListenerCookie, CallbackInfo ci, @Share("taiyitist$joinMsg") LocalRef<String> taiyitist$joinMsg) {
        // CraftBukkit start
        CraftPlayer bukkitPlayer = entityplayer.getBukkitEntity();

        // Ensure that player inventory is populated with its viewer
        entityplayer.containerMenu.transferTo(entityplayer.containerMenu, bukkitPlayer);

        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(bukkitPlayer, taiyitist$joinMsg.get());
        cserver.getPluginManager().callEvent(playerJoinEvent);

        if (!entityplayer.connection.isAcceptingMessages()) {
            return;
        }

        taiyitist$joinMsg.set(playerJoinEvent.getJoinMessage());

        if (taiyitist$joinMsg.get() != null && taiyitist$joinMsg.get().length() > 0) {
            for (Component line : org.bukkit.craftbukkit.util.CraftChatMessage.fromString(taiyitist$joinMsg.get())) {
                server.getPlayerList().broadcastSystemMessage(line, false);
            }
        }
        // CraftBukkit end

        // CraftBukkit start - sendAll above replaced with this loop
        ClientboundPlayerInfoUpdatePacket packet = ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(entityplayer));

        for (int i = 0; i < this.players.size(); ++i) {
            ServerPlayer entityplayer1 = (ServerPlayer) this.players.get(i);

            if (entityplayer1.getBukkitEntity().canSee(bukkitPlayer)) {
                entityplayer1.connection.send(packet);
            }

            if (!bukkitPlayer.canSee(entityplayer1.getBukkitEntity())) {
                continue;
            }

            entityplayer.connection.send(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(entityplayer1)));
        }
        entityplayer.taiyitist$setSentListPacket(true);
        // CraftBukkit end

        entityplayer.refreshEntityData(entityplayer); // CraftBukkit - BungeeCord#2321, send complete data to self on spawn
    }

    @WrapWithCondition(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addNewPlayer(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private boolean taiyitist$checkAddPlayer(ServerLevel instance, ServerPlayer serverPlayer, @Local(ordinal = 1) ServerLevel serverLevel2) {
        return serverPlayer.level() == serverLevel2 && !serverLevel2.players().contains(serverPlayer);
    }

    @ModifyVariable(method = "placeNewPlayer", ordinal = 1, at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/server/level/ServerLevel;addNewPlayer(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private ServerLevel taiyitist$handleWorldChanges(ServerLevel value, Connection connection, ServerPlayer player) {
        return player.level();// CraftBukkit - Update in case join event changed it
    }

    @WrapWithCondition(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/bossevents/CustomBossEvents;onPlayerConnect(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private boolean taiyitist$checkOnPlayerConnect(CustomBossEvents instance, ServerPlayer serverPlayer, @Local(ordinal = 1) ServerLevel serverLevel2) {
        return serverPlayer.level() == serverLevel2 && !serverLevel2.players().contains(serverPlayer);
    }

    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;initInventoryMenu()V", shift = At.Shift.AFTER))
    private void taiyitist$addInfoLog(Connection connection, ServerPlayer serverPlayer, CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
        LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", serverPlayer.getName().getString(), connection.getLoggableAddress(this.server.logIPs()), serverPlayer.getId(), serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ());
    }

    @Nullable
    @Override
    public ServerPlayer canPlayerLogin(ServerLoginPacketListenerImpl loginlistener, GameProfile gameprofile) {
        MutableComponent mutableComponent;
        // Moved from processLogin
        UUID uuid = gameprofile.getId();
        Set<ServerPlayer> set = Sets.newIdentityHashSet();

        for (ServerPlayer entityplayer : this.players) {
            if (entityplayer.getUUID().equals(uuid)) {
                set.add(entityplayer);
            }
        }

        for (ServerPlayer entityplayer2 : set) {
            save(entityplayer2); // CraftBukkit - Force the player's inventory to be saved
            entityplayer2.connection.disconnect(PlayerList.DUPLICATE_LOGIN_DISCONNECT_MESSAGE);
        }

        // Instead of kicking then returning, we need to store the kick reason
        // in the event, check with plugins to see if it's ok, and THEN kick
        // depending on the outcome.
        SocketAddress socketaddress = loginlistener.connection.getRemoteAddress();

        ServerPlayer entity = new ServerPlayer(this.server, this.server.getLevel(Level.OVERWORLD), gameprofile, ClientInformation.createDefault());
        entity.taiyitist$setTransferCookieConnection((CraftPlayer.TransferCookieConnection) loginlistener);
        org.bukkit.entity.Player player = entity.getBukkitEntity();
        PlayerLoginEvent event = new PlayerLoginEvent(player, loginlistener.connection.bridge$hostname(), ((java.net.InetSocketAddress) socketaddress).getAddress());

        if (this.bans.isBanned(gameprofile)) {
            UserBanListEntry userBanListEntry = (UserBanListEntry)this.bans.get(gameprofile);
            mutableComponent = Component.translatable("multiplayer.disconnect.banned.reason", new Object[]{userBanListEntry.getReason()});
            if (userBanListEntry.getExpires() != null) {
                mutableComponent.append(Component.translatable("multiplayer.disconnect.banned.expiration", new Object[]{BAN_DATE_FORMAT.format(userBanListEntry.getExpires())}));
            }

            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, CraftChatMessage.fromComponent(mutableComponent));
        } else if (!this.isWhiteListed(gameprofile)) {
            mutableComponent = Component.translatable("multiplayer.disconnect.not_whitelisted");
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, CraftChatMessage.fromComponent(mutableComponent));
        } else if (this.ipBans.isBanned(socketaddress)) {
            IpBanListEntry ipBanListEntry = this.ipBans.get(socketaddress);
            mutableComponent = Component.translatable("multiplayer.disconnect.banned_ip.reason", new Object[]{ipBanListEntry.getReason()});
            if (ipBanListEntry.getExpires() != null) {
                mutableComponent.append(Component.translatable("multiplayer.disconnect.banned_ip.expiration", new Object[]{BAN_DATE_FORMAT.format(ipBanListEntry.getExpires())}));
            }

            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, CraftChatMessage.fromComponent(mutableComponent));
        } else {
            if (this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(gameprofile)) {
                event.disallow(PlayerLoginEvent.Result.KICK_FULL, "The server is full");
            }
        }

        cserver.getPluginManager().callEvent(event);
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            loginlistener.disconnect(event.getKickMessage());
            return null;
        }
        return entity;
    }

    // CraftBukkit start - add a world/entity limited version
    @Override
    public void broadcastAll(Packet packet, Player entityhuman) {
        for (int i = 0; i < this.players.size(); ++i) {
            ServerPlayer entityplayer =  this.players.get(i);
            if (entityhuman != null && !entityplayer.getBukkitEntity().canSee(entityhuman.getBukkitEntity())) {
                continue;
            }
            ((ServerPlayer) this.players.get(i)).connection.send(packet);
        }
    }

    @Override
    public void broadcastAll(Packet packet, Level world) {
        for (int i = 0; i < world.players().size(); ++i) {
            ((ServerPlayer) world.players().get(i)).connection.send(packet);
        }

    }
    // CraftBukkit end

    // CraftBukkit start
    @Override
    public void broadcastMessage(Component[] iChatBaseComponents) {
        for (Component component : iChatBaseComponents) {
            broadcastSystemMessage(component, false);
        }
    }
    // CraftBukkit end


    @Override
    public CraftServer getCraftServer() {
        return this.cserver;
    }

    @Override
    public void reloadRecipes() {
        RecipeManager recipeManager = this.server.getRecipeManager();
        ClientboundUpdateRecipesPacket clientboundUpdateRecipesPacket = new ClientboundUpdateRecipesPacket(recipeManager.getSynchronizedItemProperties(), recipeManager.getSynchronizedStonecutterRecipes());

        for (ServerPlayer serverPlayer : this.players) {
            serverPlayer.connection.send(clientboundUpdateRecipesPacket);
            serverPlayer.getRecipeBook().sendInitialRecipeBook(serverPlayer);
        }
    }
}
