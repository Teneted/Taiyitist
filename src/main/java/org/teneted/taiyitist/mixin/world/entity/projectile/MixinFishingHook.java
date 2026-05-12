package org.teneted.taiyitist.mixin.world.entity.projectile;

import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.projectile.InjectionFishingHook;

@Mixin(FishingHook.class)
public class MixinFishingHook implements InjectionFishingHook {
}
