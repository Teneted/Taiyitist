package org.teneted.taiyitist.injection.commands;

import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSource;

public interface InjectionCommandSourceStack {

    default void taiyitist$setSource(CommandSource source) {
    }

    default boolean hasPermission(int i, String bukkitPermission) {
        return false;
    }

    default org.bukkit.command.CommandSender taiyitist$getBukkitSender() {
        return null;
    }

    default CommandNode<?> bridge$getCurrentCommand() {
        return null;
    }

    default void taiyitist$setCurrentCommand(CommandNode<?> node) {
    }
}
