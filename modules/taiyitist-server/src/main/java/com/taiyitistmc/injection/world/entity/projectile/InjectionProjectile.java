package com.taiyitistmc.injection.world.entity.projectile;

import net.minecraft.world.phys.HitResult;

public interface InjectionProjectile {

    default boolean hitCancelled() {
        return false;
    }

    default void taiyitist$setHitCancelled(boolean cancelled) {

    }

    default void preOnHit(HitResult movingobjectposition) {

    }
}
