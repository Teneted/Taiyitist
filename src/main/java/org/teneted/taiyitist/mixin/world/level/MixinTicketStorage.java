package org.teneted.taiyitist.mixin.world.level;

import net.minecraft.world.level.TicketStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.InjectionTicketStorage;

@Mixin(TicketStorage.class)
public class MixinTicketStorage implements InjectionTicketStorage {
}
