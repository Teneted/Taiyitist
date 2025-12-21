package org.celestial_artistry.taiyitist.mixin.server.commands;

import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.WorldBorderCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldBorderCommand.class)
public class MixinWorldBorderCommand {

    @Unique
    private static AtomicReference<CommandSourceStack> taiyitist$source = new AtomicReference<>();

    @Inject(method = "setDamageBuffer", at = @At("HEAD"))
    private static void taiyitist$setSource(CommandSourceStack source, float distance, CallbackInfoReturnable<Integer> cir) {
        taiyitist$source.set(source);
    }

    @Redirect(method = "setDamageBuffer", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder taiyitist$resetBorder(ServerLevel instance) {
        return taiyitist$source.get().getLevel().getWorldBorder();
    }

    @Inject(method = "setDamageAmount", at = @At("HEAD"))
    private static void taiyitist$setSource0(CommandSourceStack source, float distance, CallbackInfoReturnable<Integer> cir) {
        taiyitist$source.set(source);
    }

    @Redirect(method = "setDamageAmount", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder taiyitist$resetBorder0(ServerLevel instance) {
        return taiyitist$source.get().getLevel().getWorldBorder();
    }

    @Inject(method = "setWarningTime", at = @At("HEAD"))
    private static void taiyitist$setSource1(CommandSourceStack source, int time, CallbackInfoReturnable<Integer> cir) {
        taiyitist$source.set(source);
    }

    @Redirect(method = "setWarningTime", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder taiyitist$resetBorder1(ServerLevel instance) {
        return taiyitist$source.get().getLevel().getWorldBorder();
    }

    @Inject(method = "setWarningDistance", at = @At("HEAD"))
    private static void taiyitist$setSource2(CommandSourceStack source, int time, CallbackInfoReturnable<Integer> cir) {
        taiyitist$source.set(source);
    }

    @Redirect(method = "setWarningDistance", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder taiyitist$resetBorder2(ServerLevel instance) {
        return taiyitist$source.get().getLevel().getWorldBorder();
    }

    @Inject(method = "getSize", at = @At("HEAD"))
    private static void taiyitist$setSource3(CommandSourceStack source, CallbackInfoReturnable<Integer> cir) {
        taiyitist$source.set(source);
    }

    @Redirect(method = "getSize", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder taiyitist$resetBorder3(ServerLevel instance) {
        return taiyitist$source.get().getLevel().getWorldBorder();
    }

    @Inject(method = "setCenter", at = @At("HEAD"))
    private static void taiyitist$setSource4(CommandSourceStack source, Vec2 pos, CallbackInfoReturnable<Integer> cir) {
        taiyitist$source.set(source);
    }

    @Redirect(method = "setCenter", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder taiyitist$resetBorder4(ServerLevel instance) {
        return taiyitist$source.get().getLevel().getWorldBorder();
    }

    @Inject(method = "setSize", at = @At("HEAD"))
    private static void taiyitist$setSource5(CommandSourceStack source, double newSize, long time, CallbackInfoReturnable<Integer> cir) {
        taiyitist$source.set(source);
    }

    @Redirect(method = "setSize", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder taiyitist$resetBorder5(ServerLevel instance) {
        return taiyitist$source.get().getLevel().getWorldBorder();
    }
}
