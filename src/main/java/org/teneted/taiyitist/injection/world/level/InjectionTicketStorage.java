package org.teneted.taiyitist.injection.world.level;

import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;

public interface InjectionTicketStorage {

    default boolean addPluginRegionTicket(final ChunkPos pos, final org.bukkit.plugin.Plugin value) {
        throw new IllegalStateException("Not implemented");
    }

    default boolean removePluginRegionTicket(final ChunkPos pos, final org.bukkit.plugin.Plugin value) {
        throw new IllegalStateException("Not implemented");
    }

    default void removeAllPluginRegionTickets(TicketType ticketType, int ticketLevel, org.bukkit.plugin.Plugin ticketIdentifier) {
        throw new IllegalStateException("Not implemented");
    }
}
