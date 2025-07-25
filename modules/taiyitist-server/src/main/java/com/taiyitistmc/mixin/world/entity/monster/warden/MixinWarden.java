package com.taiyitistmc.mixin.world.entity.monster.warden;

import com.taiyitistmc.bukkit.BukkitSnapshotCaptures;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.phys.Vec3;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Warden.class)
public class MixinWarden {

    @Inject(method = "applyDarknessAround", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectUtil;addEffectToPlayersAround(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;DLnet/minecraft/world/effect/MobEffectInstance;I)Ljava/util/List;"))
    private static void taiyitist$reason(ServerLevel serverLevel, Vec3 vec3, Entity entity, int f, CallbackInfo ci) {
        BukkitSnapshotCaptures.captureEffectCause(EntityPotionEffectEvent.Cause.WARDEN);
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.pushEffectCause(EntityPotionEffectEvent.Cause.WARDEN);
        }
    }
}
