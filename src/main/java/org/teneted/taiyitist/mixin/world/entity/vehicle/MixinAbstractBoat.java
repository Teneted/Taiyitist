package org.teneted.taiyitist.mixin.world.entity.vehicle;

import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.vehicle.InjectionAbstractBoat;

@Mixin(AbstractBoat.class)
public class MixinAbstractBoat implements InjectionAbstractBoat {
}
