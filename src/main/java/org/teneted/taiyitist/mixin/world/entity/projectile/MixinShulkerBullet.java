package org.teneted.taiyitist.mixin.world.entity.projectile;

import net.minecraft.world.entity.projectile.ShulkerBullet;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.projectile.InjectionShulkerBullet;

@Mixin(ShulkerBullet.class)
public class MixinShulkerBullet implements InjectionShulkerBullet {
}
