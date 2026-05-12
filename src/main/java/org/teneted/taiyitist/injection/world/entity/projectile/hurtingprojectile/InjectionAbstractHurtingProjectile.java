package org.teneted.taiyitist.injection.world.entity.projectile.hurtingprojectile;

public interface InjectionAbstractHurtingProjectile {

    default void setDirection(double d3, double d4, double d5) {
        throw new IllegalStateException("Not implemented");
    }

    default float bridge$bukkitYield() {
        throw new IllegalStateException("Not implemented");
    }

    default boolean bridge$isIncendiary() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setBukkitYield(float yield) {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setIsIncendiary(boolean incendiary) {
        throw new IllegalStateException("Not implemented");
    }
}
