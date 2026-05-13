package org.teneted.taiyitist.injection.commands;

import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSource;

public interface InjectionCommandSourceStack {

    default void taiyitist$setSource(CommandSource source) {
        throw new IllegalStateException("Not implemented");
    }

    default org.bukkit.command.CommandSender getBukkitSender() {
        throw new IllegalStateException("Not implemented");
    }

    default CommandNode<?> bridge$getCurrentCommand() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setCurrentCommand(CommandNode<?> node) {
        throw new IllegalStateException("Not implemented");
    }
}
