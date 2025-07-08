package com.taiyitistmc.mixin.server.level;

import com.taiyitistmc.asm.annotation.TransformAccess;
import net.minecraft.server.level.TicketType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TicketType.class)
public abstract class MixinTicketType {

    @Shadow
    private static TicketType register(String string, long l, boolean bl, TicketType.TicketUse ticketUse) {
        return null;
    }

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static long pluginTimeout = 0L;
    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static final TicketType PLUGIN = register("plugin", 0L, false, TicketType.TicketUse.LOADING_AND_SIMULATION);
    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static final TicketType PLUGIN_TICKET = register("plugin_ticket", 0L, false, TicketType.TicketUse.LOADING_AND_SIMULATION);
}
