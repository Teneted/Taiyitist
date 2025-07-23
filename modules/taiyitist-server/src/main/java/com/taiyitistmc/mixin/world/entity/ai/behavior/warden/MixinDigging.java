package com.taiyitistmc.mixin.world.entity.ai.behavior.warden;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.warden.Digging;
import net.minecraft.world.entity.monster.warden.Warden;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Digging.class)
public class MixinDigging<E extends Warden> {

    @Inject(method = "stop(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/monster/warden/Warden;J)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/warden/Warden;remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    private void taiyitist$pushDigReason(ServerLevel serverLevel, E warden, long l, CallbackInfo ci) {
        warden.pushRemoveCause(EntityRemoveEvent.Cause.DESPAWN);// CraftBukkit - Add bukkit remove cause
    }
}
