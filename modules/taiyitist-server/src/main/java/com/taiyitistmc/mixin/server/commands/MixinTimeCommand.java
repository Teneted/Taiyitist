package com.taiyitistmc.mixin.server.commands;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.TimeCommand;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Bukkit;
import org.bukkit.event.world.TimeSkipEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TimeCommand.class)
public abstract class MixinTimeCommand {

    @Shadow
    private static int getDayTime(ServerLevel serverLevel) {
        return 0;
    }

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    public static int setTime(CommandSourceStack commandSourceStack, int i) {
        // CraftBukkit start - SPIGOT-6496: Only set the time for the world the command originates in
        {
            ServerLevel worldserver = commandSourceStack.getLevel();
            TimeSkipEvent event = new TimeSkipEvent(worldserver.getWorld(), TimeSkipEvent.SkipReason.COMMAND, i - worldserver.getDayTime());
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                worldserver.setDayTime((long) worldserver.getDayTime() + event.getSkipAmount());
            }
            // CraftBukkit end
        }

        commandSourceStack.getServer().forceTimeSynchronization();
        commandSourceStack.sendSuccess(() -> {
            return Component.translatable("commands.time.set", new Object[]{i});
        }, true);
        return getDayTime(commandSourceStack.getLevel());
    }

    /**
     * @author wdog5
     * @reason bukkit
     */
    @Overwrite
    public static int addTime(CommandSourceStack commandSourceStack, int i) {
        // CraftBukkit start - SPIGOT-6496: Only set the time for the world the command originates in
        {
            ServerLevel worldserver = commandSourceStack.getLevel();
            TimeSkipEvent event = new TimeSkipEvent(worldserver.getWorld(), TimeSkipEvent.SkipReason.COMMAND, i);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                worldserver.setDayTime(worldserver.getDayTime() + event.getSkipAmount());
            }
            // CraftBukkit end
        }
        commandSourceStack.getServer().forceTimeSynchronization();
        int j = getDayTime(commandSourceStack.getLevel());
        commandSourceStack.sendSuccess(() -> {
            return Component.translatable("commands.time.set", new Object[]{j});
        }, true);
        return j;
    }
}
