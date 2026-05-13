package org.teneted.taiyitist.mixin.commands;

import net.minecraft.commands.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.command.ServerCommandSender;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.commands.InjectionCommandSource;

@Mixin(targets = "net/minecraft/commands/CommandSource$1")
public class MixinCommandSource1 implements InjectionCommandSource {

    public CommandSender getBukkitSender(CommandSourceStack wrapper) {
        return new ServerCommandSender() {
            private final boolean isOp = wrapper.getPlayer().getBukkitEntity().hasPermission("minecraft.admin.command_feedback");

            @Override
            public boolean isOp() {
                return isOp;
            }

            @Override
            public void setOp(boolean value) {
            }

            @Override
            public void sendMessage(@NotNull String message) {

            }

            @Override
            public void sendMessage(@NotNull String[] messages) {

            }

            @NotNull
            @Override
            public String getName() {
                return "NULL";
            }
        };
    }

    @Override
    public CommandSender taiyitist$getBukkitSender(CommandSourceStack wrapper) {
        return getBukkitSender(wrapper);
    }
}
