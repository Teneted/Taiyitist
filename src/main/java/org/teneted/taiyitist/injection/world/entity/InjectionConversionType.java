package org.teneted.taiyitist.injection.world.entity;

import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.Mob;

public interface InjectionConversionType {

    default void postConvert(Mob entityinsentient, Mob entityinsentient1, ConversionParams conversionparams) {

    }
    // CraftBukkit

}
