package org.teneted.taiyitist.mixin.server.level;

import net.minecraft.server.level.Ticket;
import net.minecraft.server.level.TicketType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.asm.annotation.TransformAccess;
import org.teneted.taiyitist.injection.server.level.InjectionTicket;

@Mixin(Ticket.class)
public class MixinTicket implements InjectionTicket {

    // CraftBukkit start
    public Object key;

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static Ticket of(TicketType tickettype, int i, Object key) {
        Ticket ticket = new Ticket(tickettype, i);
        ticket.taiyitist$setKey(key);
        return ticket;
    }
    // CraftBukkit end


    @Override
    public Object bridge$key() {
        return this.key;
    }

    @Override
    public void taiyitist$setKey(Object key) {
        this.key = key;
    }
}
