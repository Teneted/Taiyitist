package org.teneted.taiyitist.mixin.world.level.portal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.teneted.taiyitist.asm.annotation.CreateConstructor;
import org.teneted.taiyitist.asm.annotation.ShadowConstructor;
import org.teneted.taiyitist.injection.world.level.portal.InjectionTeleportTransition;

import java.util.Set;

@Mixin(TeleportTransition.class)
public class MixinTeleportTransition implements InjectionTeleportTransition {

    @Unique
    private PlayerTeleportEvent.TeleportCause taiyitist$cause;

    @ShadowConstructor
    public void taiyitist$constructor(final ServerLevel newLevel, final Vec3 pos, final Vec3 speed, final float yRot, final float xRot, final Set<Relative> relatives, final TeleportTransition.PostTeleportTransition postTeleportTransition) {
        throw new RuntimeException();
    }

    @CreateConstructor
    public void taiyitist$constructor(final ServerLevel newLevel, final Vec3 pos, final Vec3 speed, final float yRot, final float xRot, final Set<Relative> relatives, final TeleportTransition.PostTeleportTransition postTeleportTransition, PlayerTeleportEvent.TeleportCause cause) {
        taiyitist$constructor(newLevel, pos, speed, yRot, xRot, relatives, postTeleportTransition);
        this.taiyitist$cause = cause;
    }

    @Override
    public void taiyitist$setTeleportCause(PlayerTeleportEvent.TeleportCause cause) {
        taiyitist$cause = cause;
    }

    @Override
    public PlayerTeleportEvent.TeleportCause bridge$teleportCause() {
        return taiyitist$cause == null ? PlayerTeleportEvent.TeleportCause.UNKNOWN : taiyitist$cause;
    }
}
