package org.teneted.taiyitist.mixin.world.entity.projectile.hurtingprojectile;

import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.projectile.hurtingprojectile.InjectionAbstractHurtingProjectile;

@Mixin(AbstractHurtingProjectile.class)
public class MixinAbstractHurtingProjectile implements InjectionAbstractHurtingProjectile {
}
