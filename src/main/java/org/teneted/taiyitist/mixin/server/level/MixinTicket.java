package org.teneted.taiyitist.mixin.server.level;

import net.minecraft.server.level.Ticket;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.server.level.InjectionTicket;

@Mixin(Ticket.class)
public class MixinTicket implements InjectionTicket {
}
