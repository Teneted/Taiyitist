package org.teneted.taiyitist.mixin.world.clock;

import net.minecraft.world.clock.ServerClockManager;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.clock.InjectionServerClockManager;

@Mixin(ServerClockManager.class)
public class MixinServerClockManager implements InjectionServerClockManager {
}
