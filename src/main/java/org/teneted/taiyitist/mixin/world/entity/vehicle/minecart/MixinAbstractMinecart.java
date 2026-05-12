package org.teneted.taiyitist.mixin.world.entity.vehicle.minecart;

import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.vehicle.minecart.InjectionAbstractMinecart;

@Mixin(AbstractMinecart.class)
public class MixinAbstractMinecart implements InjectionAbstractMinecart {
}
