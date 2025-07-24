package com.taiyitistmc.mixin.world.entity.raid;

import com.llamalad7.mixinextras.sugar.Local;
import com.taiyitistmc.injection.world.entity.raid.InjectionRaid;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.raid.RaidStopEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(Raid.class)
public class MixinRaid implements InjectionRaid {

    @Shadow private Raid.RaidStatus status;

    @Shadow @Final private Map<Integer, Set<Raider>> groupRaiderMap;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;stop()V", ordinal = 0))
    private void taiyitist$callRaidEvent(ServerLevel serverLevel, CallbackInfo ci) {
        CraftEventFactory.callRaidStopEvent(((Raid) (Object) this), serverLevel, RaidStopEvent.Reason.PEACE); // CraftBukkit
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;stop()V", ordinal = 1))
    private void taiyitist$callRaidEvent0(ServerLevel serverLevel, CallbackInfo ci) {
        CraftEventFactory.callRaidStopEvent(((Raid) (Object) this), serverLevel, RaidStopEvent.Reason.NOT_IN_VILLAGE); // CraftBukkit
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/raid/Raid;status:Lnet/minecraft/world/entity/raid/Raid$RaidStatus;", ordinal = 1))
    private void taiyitist$callRaidEvent1(ServerLevel serverLevel, CallbackInfo ci) {
        org.bukkit.craftbukkit.event.CraftEventFactory.callRaidFinishEvent(((Raid) (Object) this), serverLevel, new java.util.ArrayList<>()); // CraftBukkit
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;stop()V", ordinal = 2))
    private void taiyitist$callRaidEvent2(ServerLevel serverLevel, CallbackInfo ci) {
        CraftEventFactory.callRaidStopEvent(((Raid) (Object) this), serverLevel, RaidStopEvent.Reason.TIMEOUT); // CraftBukkit
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;stop()V", ordinal = 3))
    private void taiyitist$callRaidEvent3(ServerLevel serverLevel, CallbackInfo ci) {
        CraftEventFactory.callRaidStopEvent(((Raid) (Object) this), serverLevel, RaidStopEvent.Reason.UNSPAWNABLE); // CraftBukkit
    }

    @Unique
    List<Player> winners = new java.util.ArrayList<>(); // CraftBukkit
    // CraftBukkit start
    @Unique
    Raider leader = null;
    @Unique
    List<Raider> raiders = new java.util.ArrayList<>();
    // CraftBukkit end

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/PlayerTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void taiyitist$collectWinners(ServerLevel serverLevel, CallbackInfo ci, @Local ServerPlayer serverPlayer) {
        winners.add(serverPlayer.getBukkitEntity()); // CraftBukkit
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;setDirty(Lnet/minecraft/server/level/ServerLevel;)V"))
    private void taiyitist$callRaidEvent4(ServerLevel serverLevel, CallbackInfo ci) {
        CraftEventFactory.callRaidFinishEvent(((Raid) (Object) this), serverLevel, winners); // CraftBukkit
    }

    @Inject(method = "spawnGroup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;setLeader(ILnet/minecraft/world/entity/raid/Raider;)V"))
    private void taiyitist$setRaiders(ServerLevel serverLevel, BlockPos blockPos, CallbackInfo ci, @Local Raider raider) {
        leader = raider; // CraftBukkit
    }

    @Inject(method = "spawnGroup", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/EntityType;RAVAGER:Lnet/minecraft/world/entity/EntityType;"))
    private void taiyitist$collectRaiders(ServerLevel serverLevel, BlockPos blockPos, CallbackInfo ci, @Local Raider raider) {
        raiders.add(raider); // CraftBukkit
    }

    @Inject(method = "spawnGroup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raider;startRiding(Lnet/minecraft/world/entity/Entity;)Z", shift = At.Shift.AFTER))
    private void taiyitist$collectRaiders0(ServerLevel serverLevel, BlockPos blockPos, CallbackInfo ci, @Local(ordinal = 1) Raider raider) {
        raiders.add(raider); // CraftBukkit
    }

    @Inject(method = "spawnGroup", at = @At("RETURN"))
    private void taiyitist$callRaidEvent5(ServerLevel serverLevel, BlockPos blockPos, CallbackInfo ci) {
        CraftEventFactory.callRaidSpawnWaveEvent(((Raid) (Object) this), serverLevel, leader, raiders); // CraftBukkit
    }

    @Inject(method = "joinRaid", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"))
    private void taiyitist$pushSpawnReason(ServerLevel serverLevel, int i, Raider raider, BlockPos blockPos, boolean bl, CallbackInfo ci) {
        serverLevel.pushAddEntityReason(CreatureSpawnEvent.SpawnReason.RAID); // CraftBukkit
    }

    // CraftBukkit start
    @Override
    public boolean isInProgress() {
        return this.status == Raid.RaidStatus.ONGOING;
    }
    // CraftBukkit end

    // CraftBukkit start - a method to get all raiders
    @Override
    public java.util.Collection<Raider> getRaiders() {
        return this.groupRaiderMap.values().stream().flatMap(Set::stream).collect(java.util.stream.Collectors.toSet());
    }
    // CraftBukkit end
}
