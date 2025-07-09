package com.taiyitistmc.mixin.util;

import com.taiyitistmc.injection.util.InjectionTickThrottler;
import net.minecraft.util.TickThrottler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TickThrottler.class)
public class MixinTickThrottler implements InjectionTickThrottler {

    @Shadow private int count;

    @Shadow @Final private int incrementStep;

    @Shadow @Final private int threshold;

    @Override
    public boolean isIncrementAndUnderThreshold() {
        return isIncrementAndUnderThreshold(this.incrementStep, this.threshold);
    }

    @Override
    public boolean isIncrementAndUnderThreshold(int incrementStep, int threshold) {
        return this.count < threshold;
        // CraftBukkit end
    }
}
