package org.teneted.taiyitist.injection.world.entity;

import net.minecraft.world.entity.animal.allay.Allay;

public interface InjectionAllay {

    default Allay duplicateAllay0() {
        return null;
    }

    default void setCanDuplicate(boolean canDuplicate) {
    }

    default boolean bridge$forceDancing() {
        return false;
    }

    default void taiyitist$setForceDancing(boolean forceDancing) {

    }
}
