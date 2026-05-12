package org.teneted.taiyitist.mixin.world.entity;

import net.minecraft.world.entity.LightningBolt;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.InjectionLightningBolt;

@Mixin(LightningBolt.class)
public class MixinLightningBolt implements InjectionLightningBolt {
}
