package org.teneted.taiyitist.mixin.world.level;

import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Level.ExplosionInteraction.class)
public enum MixinExplosionInteraction {

    STANDARD("standard");

    @SuppressWarnings("all")
    @Shadow
    MixinExplosionInteraction(final String id) {

    }
}
