package com.taiyitistmc.mixin.server.level;

import com.taiyitistmc.injection.server.level.InjectionServerEntity;
import java.util.Set;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkMap.TrackedEntity.class)
public class MixinChunkMap_TrackedEntity {


    @Shadow @Final
    ServerEntity serverEntity;
    @Shadow @Final
    Entity entity;

    @Shadow @Final public Set<ServerPlayerConnection> seenBy;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void banner$setTrackedPlayers(ChunkMap outer, Entity entity, int range, int updateFrequency, boolean sendVelocityUpdates, CallbackInfo ci) {
        ((InjectionServerEntity) this.serverEntity).setTrackedPlayers(this.seenBy);
    }

    @Inject(method = "updatePlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerEntity;addPairing(Lnet/minecraft/server/level/ServerPlayer;)V"), cancellable = true)
    private void banner$trackEvent(ServerPlayer serverPlayer, CallbackInfo ci) {
        // Paper start
        if (!(io.papermc.paper.event.player.PlayerTrackEntityEvent.getHandlerList().getRegisteredListeners().length == 0 || new io.papermc.paper.event.player.PlayerTrackEntityEvent(serverPlayer.getBukkitEntity(), entity.getBukkitEntity()).callEvent())) {
            ci.cancel();
        }
        // Paper end
    }

    @Inject(method = "updatePlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerEntity;removePairing(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void banner$untrackEvent(ServerPlayer serverPlayer, CallbackInfo ci) {
        // Paper start
        if (io.papermc.paper.event.player.PlayerUntrackEntityEvent.getHandlerList().getRegisteredListeners().length > 0) {
            new io.papermc.paper.event.player.PlayerUntrackEntityEvent(serverPlayer.getBukkitEntity(), this.entity.getBukkitEntity()).callEvent();
        }
        // Paper end
    }

}
