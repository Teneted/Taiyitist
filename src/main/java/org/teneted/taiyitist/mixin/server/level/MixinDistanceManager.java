package org.teneted.taiyitist.mixin.server.level;

import net.minecraft.server.level.DistanceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.server.level.InjectionDistanceManager;

@Mixin(DistanceManager.class)
public class MixinDistanceManager implements InjectionDistanceManager {
}
