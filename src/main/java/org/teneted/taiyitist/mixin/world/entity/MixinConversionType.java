package org.teneted.taiyitist.mixin.world.entity;

import net.minecraft.world.entity.ConversionType;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.InjectionConversionType;

@Mixin(ConversionType.class)
public class MixinConversionType implements InjectionConversionType {
}
