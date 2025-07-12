package com.taiyitistmc.mixin.server.level;

import com.taiyitistmc.asm.annotation.CreateConstructor;
import com.taiyitistmc.asm.annotation.ShadowConstructor;
import com.taiyitistmc.injection.server.level.InjectionServerEntity;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

// Taiyitist - TODO fix mixins
@Mixin(ServerEntity.class)
public class MixinServerEntity implements InjectionServerEntity {

    @Shadow @Final private BiConsumer<Packet<?>, List<UUID>> broadcastWithIgnore;
    @Shadow @Final private Entity entity;
    // CraftBukkit start
    private Set<ServerPlayerConnection> trackedPlayers;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void taiyitist$init(ServerLevel serverLevel, Entity entity, int i, boolean bl, Consumer consumer, BiConsumer biConsumer, CallbackInfo ci) {
        trackedPlayers = new HashSet<>();
    }

    @ShadowConstructor
    public void taiyitist$constructor(ServerLevel serverLevel, Entity entity, int updateFrequency, boolean sendVelocityUpdates, Consumer<Packet<?>> consumer, BiConsumer<Packet<?>, List<UUID>> biConsumer) {
        throw new NullPointerException();
    }

    @CreateConstructor
    public void taiyitist$constructor(ServerLevel serverLevel, Entity entity, int updateFrequency, boolean sendVelocityUpdates, Consumer<Packet<?>> packetConsumer, BiConsumer<Packet<?>, List<UUID>> biConsumer, Set<ServerPlayerConnection> set) {
        taiyitist$constructor(serverLevel, entity, updateFrequency, sendVelocityUpdates, packetConsumer, biConsumer);
        this.trackedPlayers = set;
    }

    // CraftBukkit start
    private void broadcastWithIgnoreAndSend(Packet<?> packet, List<UUID> list) {
        this.broadcastWithIgnore.accept(packet, list);
        if (this.entity instanceof ServerPlayer) {
            ((ServerPlayer) this.entity).connection.send(packet);
        }

    }
    // CraftBukkit end

    @Override
    public void setTrackedPlayers(Set<ServerPlayerConnection> trackedPlayers) {
        this.trackedPlayers = trackedPlayers;
    }
}
