package org.teneted.taiyitist.mixin.util;

import net.minecraft.util.TickThrottler;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.util.InjectionTickThrottler;

@Mixin(TickThrottler.class)
public class MixinTickThrottler implements InjectionTickThrottler {
}
