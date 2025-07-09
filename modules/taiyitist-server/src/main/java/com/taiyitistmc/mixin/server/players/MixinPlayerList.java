package com.taiyitistmc.mixin.server.players;

import com.llamalad7.mixinextras.sugar.Local;
import com.taiyitistmc.TaiyitistMod;
import com.taiyitistmc.fabric.BukkitRegistry;
import com.taiyitistmc.injection.server.players.InjectionPlayerList;
import com.taiyitistmc.util.I18n;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.server.players.IpBanList;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserBanList;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.ValueInput;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList implements InjectionPlayerList {

    @Shadow
    @Final
    private static SimpleDateFormat BAN_DATE_FORMAT;
    @Mutable
    @Shadow
    @Final
    public List<ServerPlayer> players;
    @Shadow
    public int maxPlayers;
    @Shadow
    @Final
    public PlayerDataStorage playerIo;
    public String quitMsg;
    @Unique
    public AtomicReference<ServerLoginPacketListenerImpl> handler = new AtomicReference<>(null);
    public ServerLevel taiyitist$worldserver = null;
    public AtomicBoolean avoidSuffocation = new AtomicBoolean(true);
    @Shadow
    @Final
    private Map<UUID, ServerPlayer> playersByUUID;
    @Shadow
    @Final
    private MinecraftServer server;
    @Shadow
    @Final
    private UserBanList bans;
    @Shadow
    @Final
    private IpBanList ipBans;
    @Shadow
    @Final
    private Map<UUID, ServerStatsCounter> stats;

    @Shadow public abstract void broadcastSystemMessage(Component component, boolean bl);

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
}
