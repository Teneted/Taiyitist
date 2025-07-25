package com.taiyitistmc.mixin.world.entity.animal.frog;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.ShootTongue;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShootTongue.class)
public class MixinShootTongue {

    @Inject(method = "eatEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    private void taiyitist$pushRemoveCause(ServerLevel serverLevel, Frog frog, CallbackInfo ci, @Local Entity entity) {
        entity.pushRemoveCause(EntityRemoveEvent.Cause.DEATH); // CraftBukkit - add Bukkit remove cause
    }
}
