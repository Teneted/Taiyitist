package org.teneted.taiyitist.mixin.world.entity.animal.equine;

import net.minecraft.world.entity.animal.equine.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.animal.equine.InjectionAbstractHorse;

@Mixin(AbstractHorse.class)
public class MixinAbstractHorse implements InjectionAbstractHorse {
}
