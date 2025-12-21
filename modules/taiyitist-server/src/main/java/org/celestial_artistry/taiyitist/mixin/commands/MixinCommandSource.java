package org.celestial_artistry.taiyitist.mixin.commands;

import org.celestial_artistry.taiyitist.injection.commands.InjectionCommandSource;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CommandSource.class)
public interface MixinCommandSource extends InjectionCommandSource {

    @Unique
    default CommandSender getBukkitSender(CommandSourceStack wrapper) {
        return this.taiyitist$getBukkitSender(wrapper);
    }
}
