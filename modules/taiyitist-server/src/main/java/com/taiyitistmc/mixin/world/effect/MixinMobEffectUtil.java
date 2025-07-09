package com.taiyitistmc.mixin.world.effect;

import com.taiyitistmc.asm.annotation.TransformAccess;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MobEffectUtil.class)
public class MixinMobEffectUtil {

    @Inject(method = "method_42144", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private static void taiyitist$pushEffectReason(MobEffectInstance mobEffectInstance, Entity entity, ServerPlayer serverPlayer, CallbackInfo ci) {
        serverPlayer.pushEffectCause(EntityPotionEffectEvent.Cause.PLUGIN);
    }

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static List<ServerPlayer> addEffectToPlayersAround(ServerLevel worldserver, @Nullable Entity entity, Vec3 vec3d, double d0, MobEffectInstance mobeffect, int i, org.bukkit.event.entity.EntityPotionEffectEvent.Cause cause) {
        Holder<MobEffect> holder = mobeffect.getEffect();
        List<ServerPlayer> list = worldserver.getPlayers((serverPlayer) -> {
            return serverPlayer.gameMode.isSurvival() && (entity == null || !entity.isAlliedTo(serverPlayer)) && vec3d.closerThan(serverPlayer.position(), i) && (!serverPlayer.hasEffect(holder) || serverPlayer.getEffect(holder).getAmplifier() < mobeffect.getAmplifier() || serverPlayer.getEffect(holder).endsWithin(i - 1));
        });
        list.forEach((serverPlayer) -> {
            serverPlayer.addEffect(new MobEffectInstance(mobeffect), entity, cause);
        });
        return list;
    }
}
