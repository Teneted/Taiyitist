package org.celestial_artistry.taiyitist.injection.world.entity.projectile;

public interface InjectionAbstractHurtingProjectile {

    default void setDirection(double d3, double d4, double d5) {
    }

    default float bridge$bukkitYield() {
        return 0;
    }

    default boolean bridge$isIncendiary() {
        return false;
    }

    default void taiyitist$setBukkitYield(float yield) {
    }

    default void taiyitist$setIsIncendiary(boolean incendiary) {
    }
}
