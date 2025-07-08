package com.taiyitistmc.mixin.interaction;

import net.minecraft.core.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vec3i.class)
public abstract class MixinVec3i {

    @Shadow public abstract int getX();

    @Shadow public abstract int getZ();

    @Shadow public abstract int getY();

    // Paper start - Perf: Optimize isInWorldBounds
    public final boolean isInsideBuildHeightAndWorldBoundsHorizontal(final net.minecraft.world.level.LevelHeightAccessor levelHeightAccessor) {
        return this.getX() >= -30000000 && this.getZ() >= -30000000 && this.getX() < 30000000 && this.getZ() < 30000000 && !levelHeightAccessor.isOutsideBuildHeight(this.getY());
    }
    // Paper end - Perf: Optimize isInWorldBounds
}
