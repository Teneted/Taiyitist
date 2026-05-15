package org.teneted.taiyitist.mixin.commands;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.commands.InjectionCommandSource;

@Mixin(CommandSource.class)
public interface MixinCommandSource extends InjectionCommandSource {

    @Override
    default CommandSender taiyitist$getBukkitSender(CommandSourceStack wrapper) {
        return this.taiyitist$getBukkitSender(wrapper);
    }
}
