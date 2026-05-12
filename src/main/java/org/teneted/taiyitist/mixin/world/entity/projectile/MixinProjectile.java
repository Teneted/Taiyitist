package org.teneted.taiyitist.mixin.world.entity.projectile;

import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.projectile.InjectionProjectile;

@Mixin(Projectile.class)
public class MixinProjectile implements InjectionProjectile {
}
