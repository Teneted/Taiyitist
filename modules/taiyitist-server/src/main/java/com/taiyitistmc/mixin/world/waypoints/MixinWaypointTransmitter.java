package com.taiyitistmc.mixin.world.waypoints;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WaypointTransmitter.class)
public interface MixinWaypointTransmitter {

    @Inject(method = "doesSourceIgnoreReceiver", at = @At("HEAD"), cancellable = true)
    private static void taiyitist$checkCanSee(LivingEntity livingEntity, ServerPlayer serverPlayer, CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start
        if (!serverPlayer.getBukkitEntity().canSee(livingEntity.getBukkitEntity())) {
            cir.setReturnValue(true);
        }
        // CraftBukkit end
    }
}
