package com.taiyitistmc.injection.commands;

import net.minecraft.commands.CommandSourceStack;

public interface InjectionCommandSource {

    default org.bukkit.command.CommandSender taiyitist$getBukkitSender(CommandSourceStack wrapper) {
        return null;
    }
}
