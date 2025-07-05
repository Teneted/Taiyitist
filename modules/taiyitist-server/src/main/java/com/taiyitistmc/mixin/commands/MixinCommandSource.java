package com.taiyitistmc.mixin.commands;

import com.taiyitistmc.injection.commands.InjectionCommandSource;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CommandSource.class)
public interface MixinCommandSource extends InjectionCommandSource {

    @Unique
    default CommandSender getBukkitSender(CommandSourceStack wrapper) {
        return this.banner$getBukkitSender(wrapper);
    }
}
