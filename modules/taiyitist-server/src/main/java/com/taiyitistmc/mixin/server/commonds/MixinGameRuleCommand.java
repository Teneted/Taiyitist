package com.taiyitistmc.mixin.server.commonds;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.GameRuleCommand;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRuleCommand.class)
public class MixinGameRuleCommand {

    @Redirect(method = "setRule", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getRule(Lnet/minecraft/world/level/GameRules$Key;)Lnet/minecraft/world/level/GameRules$Value;"))
    private static <T extends GameRules.Value<T>> T taiyitist$allowMultiWorld(GameRules instance, GameRules.Key<T> key, @Local CommandSourceStack commandSourceStack) {
        return commandSourceStack.getLevel().getGameRules().getRule(key);
    }

    @Redirect(method = "queryRule", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getRule(Lnet/minecraft/world/level/GameRules$Key;)Lnet/minecraft/world/level/GameRules$Value;"))
    private static <T extends GameRules.Value<T>> T taiyitist$allowMultiWorld0(GameRules instance, GameRules.Key<T> key, @Local CommandSourceStack commandSourceStack) {
        return commandSourceStack.getLevel().getGameRules().getRule(key);
    }
}
