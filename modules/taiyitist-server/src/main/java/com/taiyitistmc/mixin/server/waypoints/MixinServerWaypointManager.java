package com.taiyitistmc.mixin.server.waypoints;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.waypoints.ServerWaypointManager;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerWaypointManager.class)
public class MixinServerWaypointManager {

    @Redirect(method = "isLocatorBarEnabledFor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private static boolean taiyitist$preWorld(GameRules instance, GameRules.Key<GameRules.BooleanValue> key, @Local(argsOnly = true) ServerPlayer serverPlayer) {
        return serverPlayer.level().getGameRules().getBoolean(GameRules.RULE_LOCATOR_BAR); // CraftBukkit - per-world
    }
}
