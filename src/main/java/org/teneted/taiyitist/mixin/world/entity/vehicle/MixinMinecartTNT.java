package org.teneted.taiyitist.mixin.world.entity.vehicle;

import net.minecraft.world.entity.vehicle.minecart.MinecartTNT;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.vehicle.InjectionMinecartTNT;

@Mixin(MinecartTNT.class)
public class MixinMinecartTNT implements InjectionMinecartTNT {
}
