package org.teneted.taiyitist.mixin.world.damagesource;

import net.minecraft.world.damagesource.DamageSources;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.damagesource.InjectionDamageSources;

@Mixin(DamageSources.class)
public class MixinDamageSources implements InjectionDamageSources {
}
