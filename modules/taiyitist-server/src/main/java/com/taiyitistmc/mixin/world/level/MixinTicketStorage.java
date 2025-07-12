package com.taiyitistmc.mixin.world.level;

import com.taiyitistmc.bukkit.BukkitFieldHooks;
import com.taiyitistmc.injection.world.level.InjectionTicketStorage;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.Ticket;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.TicketStorage;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.function.BiPredicate;

@Mixin(TicketStorage.class)
public abstract class MixinTicketStorage implements InjectionTicketStorage {

    @Shadow public abstract boolean addTicket(long l, Ticket ticket);

    @Shadow public abstract boolean removeTicket(long l, Ticket ticket);

    @Shadow public abstract void removeTicketIf(BiPredicate<Long, Ticket> biPredicate, @Nullable Long2ObjectOpenHashMap<List<Ticket>> long2ObjectOpenHashMap);

    @Override
    public boolean addPluginRegionTicket(ChunkPos pos, Plugin value) {
        // Keep inline with force loading
        return addTicket(pos.toLong(), new Ticket(BukkitFieldHooks.pluginTicketType(), ChunkMap.FORCED_TICKET_LEVEL));
    }

    @Override
    public boolean removePluginRegionTicket(ChunkPos pos, Plugin value) {
        // Keep inline with force loading
        return removeTicket(pos.toLong(), new Ticket(BukkitFieldHooks.pluginTicketType(), ChunkMap.FORCED_TICKET_LEVEL));
    }

    @Override
    public void removeAllPluginRegionTickets(TicketType ticketType, int ticketLevel, Plugin ticketIdentifier) {
        removeTicketIf((chunkKey, ticket) -> ticket.getType() == ticketType && ticket.getTicketLevel() == ticketLevel, null);
    }
}
