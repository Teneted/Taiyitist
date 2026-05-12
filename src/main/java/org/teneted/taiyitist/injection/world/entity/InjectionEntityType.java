package org.teneted.taiyitist.injection.world.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface InjectionEntityType<T extends Entity> {

    default @Nullable T spawn(ServerLevel level, @Nullable ItemStack itemStack, @Nullable LivingEntity user, BlockPos spawnPos, EntitySpawnReason spawnReason, boolean tryMoveDown, boolean movedUp, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason bukkitSpawnReason) {
        throw new IllegalStateException("Not implemented");
    }

    default @Nullable T spawn(ServerLevel level, BlockPos spawnPos, EntitySpawnReason spawnReason, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason bukkitSpawnReason) {
        throw new IllegalStateException("Not implemented");
    }

    default @Nullable T spawn(ServerLevel level, @Nullable Consumer<T> postSpawnConfig, BlockPos spawnPos, EntitySpawnReason spawnReason, boolean tryMoveDown, boolean movedUp, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason bukkitSpawnReason) {
        throw new IllegalStateException("Not implemented");
    }
}
