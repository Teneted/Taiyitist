package com.taiyitistmc.mixin.server.level;

import com.taiyitistmc.injection.commands.InjectionCommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.server.level.ServerPlayer$3")
public class MixinServerPlayer_3 implements InjectionCommandSource {

    @Shadow @Final
    ServerPlayer field_54403;

    @Override
    public CommandSender taiyitist$getBukkitSender(CommandSourceStack wrapper) {
        return field_54403.getBukkitEntity();
    }
}
