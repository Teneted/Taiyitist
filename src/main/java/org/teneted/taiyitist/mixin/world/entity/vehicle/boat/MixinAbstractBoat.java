package org.teneted.taiyitist.mixin.world.entity.vehicle.boat;

import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.vehicle.boat.InjectionAbstractBoat;

@Mixin(AbstractBoat.class)
public class MixinAbstractBoat implements InjectionAbstractBoat {
}
