package com.taiyitistmc.mixin.world.entity;

import com.taiyitistmc.injection.world.entity.InjectionConversionType;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.ConversionType;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ConversionType.class)
public class MixinConversionType implements InjectionConversionType {

    @Override
    public void postConvert(Mob entityinsentient, Mob entityinsentient1, ConversionParams conversionparams) {
    }
}
