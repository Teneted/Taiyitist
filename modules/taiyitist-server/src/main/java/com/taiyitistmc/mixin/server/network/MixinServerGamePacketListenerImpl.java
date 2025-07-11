package com.taiyitistmc.mixin.server.network;

import com.taiyitistmc.bukkit.BukkitFieldHooks;
import com.taiyitistmc.injection.server.network.InjectionServerCommonPacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.FutureChain;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class MixinServerGamePacketListenerImpl extends MixinServerCommonPacketListenerImpl implements InjectionServerCommonPacketListenerImpl {

    @Mutable
    @Shadow @Final private FutureChain chatMessageChain;

    // CraftBukkit start - add fields and methods
    private int lastTick = BukkitFieldHooks.currentTick();
    private int allowedPlayerTicks = 1;
    private int lastDropTick = BukkitFieldHooks.currentTick();
    private int lastBookTick  = BukkitFieldHooks.currentTick();
    private int dropCount = 0;

    private boolean hasMoved = false;
    private double lastPosX = Double.MAX_VALUE;
    private double lastPosY = Double.MAX_VALUE;
    private double lastPosZ = Double.MAX_VALUE;
    private float lastPitch = Float.MAX_VALUE;
    private float lastYaw = Float.MAX_VALUE;
    private boolean justTeleported = false;
    // CraftBukkit end

    @Override
    public CraftPlayer getCraftPlayer() {
        return (this.player == null) ? null : this.player.getBukkitEntity();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void taiyitist$init(MinecraftServer server, Connection connection,
                                ServerPlayer serverPlayer, CommonListenerCookie commonListenerCookie,
                                CallbackInfo ci) {
        allowedPlayerTicks = 1;
        dropCount = 0;
        lastPosX = Double.MAX_VALUE;
        lastPosY = Double.MAX_VALUE;
        lastPosZ = Double.MAX_VALUE;
        lastPitch = Float.MAX_VALUE;
        lastYaw = Float.MAX_VALUE;
        justTeleported = false;
        this.chatMessageChain = new FutureChain(server.bridge$chatExecutor());
        this.player = serverPlayer;
    }

}
