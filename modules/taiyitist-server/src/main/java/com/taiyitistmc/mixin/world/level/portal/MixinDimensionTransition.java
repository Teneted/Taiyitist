package com.taiyitistmc.mixin.world.level.portal;

import com.taiyitistmc.asm.annotation.CreateConstructor;
import com.taiyitistmc.asm.annotation.ShadowConstructor;
import com.taiyitistmc.injection.world.level.portal.InjectionDimensionTransition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DimensionTransition.class)
public class MixinDimensionTransition implements InjectionDimensionTransition {


    @ShadowConstructor
    public void banner$constructor(ServerLevel newLevel, Vec3 pos, Vec3 speed, float yRot, float xRot, boolean missingRespawnBlock, DimensionTransition.PostDimensionTransition postDimensionTransition) {
        throw new RuntimeException();
    }

    @CreateConstructor
    public void banner$$constructor(ServerLevel newLevel, Vec3 pos, Vec3 speed, float yRot, float xRot, boolean missingRespawnBlock, DimensionTransition.PostDimensionTransition postDimensionTransition, PlayerTeleportEvent.TeleportCause cause) {
        banner$constructor(newLevel, pos, speed, yRot, xRot, missingRespawnBlock, postDimensionTransition);
        this.banner$cause = cause;
    }

    @ShadowConstructor
    public void banner$constructor(ServerLevel serverLevel, Vec3 vec3, Vec3 vec32, float f, float g, DimensionTransition.PostDimensionTransition postDimensionTransition) {
        throw new RuntimeException();
    }

    @CreateConstructor
    public void banner$constructor(ServerLevel serverLevel, Vec3 vec3, Vec3 vec32, float f, float g, DimensionTransition.PostDimensionTransition postDimensionTransition, PlayerTeleportEvent.TeleportCause cause) {
        banner$constructor(serverLevel, vec3, vec32, f, g, postDimensionTransition);
        this.banner$cause = cause;
    }

    @Unique private PlayerTeleportEvent.TeleportCause banner$cause;

    @Override
    public void setTeleportCause(PlayerTeleportEvent.TeleportCause cause) {
        banner$cause = cause;
    }

    @Override
    public PlayerTeleportEvent.TeleportCause getTeleportCause() {
        return banner$cause == null ? PlayerTeleportEvent.TeleportCause.UNKNOWN : banner$cause;
    }
}
