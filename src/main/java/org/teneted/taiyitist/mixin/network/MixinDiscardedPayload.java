package org.teneted.taiyitist.mixin.network;

import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.network.InjectionDiscardedPayload;

@Mixin(DiscardedPayload.class)
public class MixinDiscardedPayload implements InjectionDiscardedPayload {
}
