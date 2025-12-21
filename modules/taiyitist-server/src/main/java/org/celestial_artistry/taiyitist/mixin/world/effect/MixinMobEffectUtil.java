package org.celestial_artistry.taiyitist.mixin.world.effect;

import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.world.effect.MobEffectUtil;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MobEffectUtil.class)
public class MixinMobEffectUtil {

    @Unique
    private static AtomicReference<EntityPotionEffectEvent.Cause> taiyitist$cause = new AtomicReference<>();

    /**
    @Inject(method = "addEffectToPlayersAround", locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"))
    private static void taiyitist$pushCause(ServerLevel level, Entity source, Vec3 pos, double radius, MobEffectInstance effect, int durate, CallbackInfoReturnable<List<ServerPlayer>> cir, int duration, MobEffect mobEffect, List<ServerPlayer> list) {
        EntityPotionEffectEvent.Cause cause = taiyitist$cause.get();
        cause = cause == null ? EntityPotionEffectEvent.Cause.UNKNOWN : BukkitCaptures.getEffectCause();
        if (cause != null) {
            for (ServerPlayer player : list) {
                player.pushEffectCause(cause);
            }
        }
    }*/
}
