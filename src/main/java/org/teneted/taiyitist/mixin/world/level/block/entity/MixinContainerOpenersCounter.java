package org.teneted.taiyitist.mixin.world.level.block.entity;

import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.block.entity.InjectionContainerOpenersCounter;

@Mixin(ContainerOpenersCounter.class)
public class MixinContainerOpenersCounter implements InjectionContainerOpenersCounter {
}
