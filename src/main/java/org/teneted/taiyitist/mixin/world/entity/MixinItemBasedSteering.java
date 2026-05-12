package org.teneted.taiyitist.mixin.world.entity;

import net.minecraft.world.entity.ItemBasedSteering;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.InjectionItemBasedSteering;

@Mixin(ItemBasedSteering.class)
public class MixinItemBasedSteering implements InjectionItemBasedSteering {
}
