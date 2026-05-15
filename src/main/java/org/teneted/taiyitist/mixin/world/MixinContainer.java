package org.teneted.taiyitist.mixin.world;

import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.InjectionContainer;

@Mixin(Container.class)
public interface MixinContainer extends InjectionContainer {
}
