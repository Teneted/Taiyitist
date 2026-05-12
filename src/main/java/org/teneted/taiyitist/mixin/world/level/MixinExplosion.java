package org.teneted.taiyitist.mixin.world.level;

import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.InjectionExplosion;

@Mixin(Explosion.class)
public class MixinExplosion implements InjectionExplosion {
}
