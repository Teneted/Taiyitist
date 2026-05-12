package org.teneted.taiyitist.mixin.world.damagesource;

import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.damagesource.InjectionDamageSource;

@Mixin(DamageSource.class)
public class MixinDamageSource implements InjectionDamageSource {
}
