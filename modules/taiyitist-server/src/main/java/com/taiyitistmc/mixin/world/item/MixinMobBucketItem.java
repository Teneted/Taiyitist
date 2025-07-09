package com.taiyitistmc.mixin.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobBucketItem.class)
public class MixinMobBucketItem {

    @Inject(method = "spawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"))
    private void taiyitist$pushSpawnReason(ServerLevel serverLevel, ItemStack itemStack, BlockPos blockPos, CallbackInfo ci) {
        serverLevel.pushAddEntityReason(CreatureSpawnEvent.SpawnReason.BUCKET);
    }
}
