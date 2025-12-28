package org.teneted.taiyitist.mixin.world.entity.moster;

import org.teneted.taiyitist.bukkit.BukkitSnapshotCaptures;
import net.minecraft.world.entity.monster.ElderGuardian;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ElderGuardian.class)
public class MixinElderGuardian {

    @Inject(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectUtil;addEffectToPlayersAround(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;DLnet/minecraft/world/effect/MobEffectInstance;I)Ljava/util/List;"))
    private void taiyitist$potionReason(CallbackInfo ci) {
        BukkitSnapshotCaptures.captureEffectCause(EntityPotionEffectEvent.Cause.ATTACK);
    }
}
