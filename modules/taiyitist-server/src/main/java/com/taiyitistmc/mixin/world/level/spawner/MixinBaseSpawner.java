package com.taiyitistmc.mixin.world.level.spawner;

import io.izzel.arclight.mixin.Decorate;
import io.izzel.arclight.mixin.DecorationOps;
import io.izzel.arclight.mixin.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.SpawnData;
import org.bukkit.craftbukkit.v1_20_R1.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseSpawner.class)
public abstract class MixinBaseSpawner {

    @Shadow public SimpleWeightedRandomList<SpawnData> spawnPotentials;


    @Inject(method = "setEntityId", at = @At("RETURN"))
    public void banner$clearMobs(CallbackInfo ci) {
        this.spawnPotentials = SimpleWeightedRandomList.empty();
    }

    @Decorate(method = "serverTick", inject = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;checkSpawnObstruction(Lnet/minecraft/world/level/LevelReader;)Z"))
    private void banner$nerf(@Local(ordinal = -1) Mob mob) {
        if (mob.level().bridge$spigotConfig().nerfSpawnerMobs) {
           mob.banner$setAware(false);
        }
    }

    @Decorate(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tryAddFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean banner$pushReason(ServerLevel serverLevel, Entity entity, ServerLevel level, BlockPos pos) throws Throwable {
        if (CraftEventFactory.callSpawnerSpawnEvent(entity, pos).isCancelled()) {
            throw DecorationOps.jumpToLoopStart();
        }
        serverLevel.pushAddEntityReason(CreatureSpawnEvent.SpawnReason.SPAWNER);
        return (boolean) DecorationOps.callsite().invoke(serverLevel, entity);
    }
}
