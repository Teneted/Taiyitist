package com.taiyitistmc.mixin.server.commands;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.ScheduleCommand;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.timers.TimerQueue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ScheduleCommand.class)
public class MixinScheduleCommand {

    @Redirect(method = "schedule", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/ServerLevelData;getScheduledEvents()Lnet/minecraft/world/level/timers/TimerQueue;"))
    private static TimerQueue<MinecraftServer> taiyitist$allowMultiWorld(ServerLevelData instance, @Local(argsOnly = true) CommandSourceStack commandSourceStack) {
        return commandSourceStack.getLevel().bridge$serverLevelDataCB().overworldData().getScheduledEvents();
    }
}
