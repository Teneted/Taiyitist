package com.taiyitistmc.mixin.server.commonds;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.WorldBorderCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldBorderCommand.class)
public class MixinWorldBorderCommand {

    @Redirect(method = "setDamageBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder taiyitist$allowMultiWorld(ServerLevel instance, @Local(argsOnly = true) CommandSourceStack commandSourceStack) {
        return commandSourceStack.getLevel().getWorldBorder();
    }

    @Redirect(method = "setDamageAmount", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder taiyitist$allowMultiWorld0(ServerLevel instance, @Local(argsOnly = true) CommandSourceStack commandSourceStack) {
        return commandSourceStack.getLevel().getWorldBorder();
    }

    @Redirect(method = "setWarningTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder taiyitist$allowMultiWorld1(ServerLevel instance, @Local(argsOnly = true) CommandSourceStack commandSourceStack) {
        return commandSourceStack.getLevel().getWorldBorder();
    }

    @Redirect(method = "setWarningDistance", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder taiyitist$allowMultiWorld2(ServerLevel instance, @Local(argsOnly = true) CommandSourceStack commandSourceStack) {
        return commandSourceStack.getLevel().getWorldBorder();
    }

    @Redirect(method = "getSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder taiyitist$allowMultiWorld3(ServerLevel instance, @Local(argsOnly = true) CommandSourceStack commandSourceStack) {
        return commandSourceStack.getLevel().getWorldBorder();
    }

    @Redirect(method = "setCenter", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder taiyitist$allowMultiWorld4(ServerLevel instance, @Local(argsOnly = true) CommandSourceStack commandSourceStack) {
        return commandSourceStack.getLevel().getWorldBorder();
    }

    @Redirect(method = "setSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder taiyitist$allowMultiWorld5(ServerLevel instance, @Local(argsOnly = true) CommandSourceStack commandSourceStack) {
        return commandSourceStack.getLevel().getWorldBorder();
    }
}
