package com.taiyitistmc.mixin.server.network;

import com.llamalad7.mixinextras.sugar.Local;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.networking.v1.FabricServerConfigurationNetworkHandler;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import org.bukkit.craftbukkit.CraftServerLinks;
import org.bukkit.event.player.PlayerLinksSendEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.SocketAddress;

@Mixin(ServerConfigurationPacketListenerImpl.class)
public abstract class MixinServerConfigurationPacketListenerImpl extends MixinServerCommonPacketListenerImpl implements ServerConfigurationPacketListener, TickablePacketListener, FabricServerConfigurationNetworkHandler {

    @Shadow private ClientInformation clientInformation;

    @Inject(method = "startConfiguration", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ServerLinks;isEmpty()Z"))
    private void taiyitist$serverLinkEvent(CallbackInfo ci, @Local ServerLinks serverLinks) {
        // CraftBukkit start
        CraftServerLinks wrapper = new CraftServerLinks(serverLinks);
        PlayerLinksSendEvent event = new PlayerLinksSendEvent(player.getBukkitEntity(), wrapper);
        player.getBukkitEntity().getServer().getPluginManager().callEvent(event);
        serverLinks = wrapper.getServerLinks();
        // CraftBukkit end
    }

    @Redirect(method = "handleConfigurationFinished", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;canPlayerLogin(Ljava/net/SocketAddress;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/network/chat/Component;"))
    private Component taiyitist$cancelPlayerLogin(PlayerList instance, SocketAddress socketAddress, GameProfile gameProfile) {
        return null;
    }

    @Redirect(method = "handleConfigurationFinished", at = @At(value = "NEW", target = "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/server/level/ServerLevel;Lcom/mojang/authlib/GameProfile;Lnet/minecraft/server/level/ClientInformation;)Lnet/minecraft/server/level/ServerPlayer;"))
    private ServerPlayer taiyitist$resetPlayer(MinecraftServer minecraftServer, ServerLevel serverLevel, GameProfile gameProfile, ClientInformation clientInformation) {
        return this.player;
    }

    @Inject(method = "handleConfigurationFinished", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;placeNewPlayer(Lnet/minecraft/network/Connection;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/server/network/CommonListenerCookie;)V"))
    private void taiyitist$updatePlayer(ServerboundFinishConfigurationPacket serverboundFinishConfigurationPacket, CallbackInfo ci) {
        this.player.updateOptions(this.clientInformation);
    }
}
