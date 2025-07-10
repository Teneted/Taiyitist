package com.taiyitistmc.injection.world.entity.player;

import com.taiyitistmc.injection.world.entity.InjectionLivingEntity;
import com.mojang.datafixers.util.Either;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.player.PlayerSpawnChangeEvent;

public interface InjectionPlayer extends InjectionLivingEntity {

    default boolean bridge$affectsSpawning() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setAffectsSpawning(boolean affectsSpawning) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    default CraftHumanEntity getBukkitEntity() {
        throw new IllegalStateException("Not implemented");
    }

    default void pushExhaustReason(EntityExhaustionEvent.ExhaustionReason reason) {
    }

    default Either<Player.BedSleepingProblem, Unit> startSleepInBed(BlockPos blockposition, boolean force) {
        throw new IllegalStateException("Not implemented");
    }

    default void causeFoodExhaustion(float f, EntityExhaustionEvent.ExhaustionReason reason) {
    }

    default Entity getEntityOnShoulder(CompoundTag nbttagcompound) { // CraftBukkit void->boolean
        throw new IllegalStateException("Not implemented");
    }

    default boolean bridge$fauxSleeping() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setFauxSleeping(boolean fauxSleeping) {
        throw new IllegalStateException("Not implemented");
    }

    default int bridge$oldLevel() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setOldLevel(int oldLevel) {
        throw new IllegalStateException("Not implemented");
    }

    default Player forceSleepInBed(boolean force) {
        throw new IllegalStateException("Not implemented");
    }

    default AtomicBoolean bridge$startSleepInBed_force() {
        throw new IllegalStateException("Not implemented");
    }

    default void pushSpawnChangeCause(PlayerSpawnChangeEvent.Cause cause) {
        throw new IllegalStateException("Not implemented");
    }
}
