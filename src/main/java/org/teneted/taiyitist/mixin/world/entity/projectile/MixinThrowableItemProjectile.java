package org.teneted.taiyitist.mixin.world.entity.projectile;

import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.projectile.InjectionThrowableItemProjectile;

@Mixin(ThrowableItemProjectile.class)
public class MixinThrowableItemProjectile implements InjectionThrowableItemProjectile {
}
