package com.taiyitistmc.mixin.world.item.consume_effects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.TeleportRandomlyConsumeEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TeleportRandomlyConsumeEffect.class)
public class MixinTeleportRandomlyConsumeEffect {

    @Shadow @Final private float diameter;

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    public boolean apply(Level level, ItemStack itemStack, LivingEntity livingEntity) {
        boolean bl = false;

        for(int i = 0; i < 16; ++i) {
            double d = livingEntity.getX() + (livingEntity.getRandom().nextDouble() - 0.5) * (double)this.diameter;
            double e = Mth.clamp(livingEntity.getY() + (livingEntity.getRandom().nextDouble() - 0.5) * (double)this.diameter, (double)level.getMinY(), (double)(level.getMinY() + ((ServerLevel)level).getLogicalHeight() - 1));
            double f = livingEntity.getZ() + (livingEntity.getRandom().nextDouble() - 0.5) * (double)this.diameter;
            if (livingEntity.isPassenger()) {
                livingEntity.stopRiding();
            }

            Vec3 vec3 = livingEntity.position();
            // CraftBukkit start - handle canceled status of teleport event
            if (livingEntity instanceof ServerPlayer serverPlayer) {
                serverPlayer.pushChangeDimensionCause(PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT);
            }
            java.util.Optional<Boolean> status = java.util.Optional.of(livingEntity.randomTeleport(d, e, f, true));

            if (!status.isPresent()) {
                // teleport event was canceled, no more tries
                break;
            }

            if (status.get()) {
                // CraftBukkit end
                level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(livingEntity));
                SoundSource soundSource;
                SoundEvent soundEvent;
                if (livingEntity instanceof Fox) {
                    soundEvent = SoundEvents.FOX_TELEPORT;
                    soundSource = SoundSource.NEUTRAL;
                } else {
                    soundEvent = SoundEvents.CHORUS_FRUIT_TELEPORT;
                    soundSource = SoundSource.PLAYERS;
                }

                level.playSound((Entity)null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), soundEvent, soundSource);
                livingEntity.resetFallDistance();
                bl = true;
                break;
            }
        }

        if (bl && livingEntity instanceof Player player) {
            player.resetCurrentImpulseContext();
        }

        return bl;
    }
}
