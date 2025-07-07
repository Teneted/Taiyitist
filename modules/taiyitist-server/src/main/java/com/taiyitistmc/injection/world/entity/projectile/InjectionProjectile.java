package com.taiyitistmc.injection.world.entity.projectile;

import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.phys.HitResult;

public interface InjectionProjectile {

    default boolean hitCancelled() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setHitCancelled(boolean cancelled) {
        throw new IllegalStateException("Not implemented");
    }

    default ProjectileDeflection preHitTargetOrDeflectSelf(HitResult movingobjectposition) {
        throw new IllegalStateException("Not implemented");
    }
}
