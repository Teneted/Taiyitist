package org.teneted.taiyitist.mixin.server.level;

import net.minecraft.server.level.TicketType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.teneted.taiyitist.asm.annotation.TransformAccess;
import org.teneted.taiyitist.bukkit.BukkitFieldHooks;

@Mixin(TicketType.class)
public record MixinTicketType(long timeout, @net.minecraft.server.level.TicketType.Flags int flags) {

    @Shadow
    private static TicketType register(String name, long timeout, @TicketType.Flags int flags) {
        return null;
    }

    // CraftBukkit start
    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static long pluginTimeout = 0L;
    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static final TicketType PLUGIN = register("plugin", 0L, 14);
    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static final TicketType PLUGIN_TICKET = register("plugin_ticket", 0L, 14);

    @Override
    public long timeout() {
        return (((TicketType) (Object) this) != BukkitFieldHooks.pluginTicket()) ? this.timeout : BukkitFieldHooks.pluginTimeout();
    }
    // CraftBukkit end
}
