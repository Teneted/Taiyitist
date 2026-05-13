package org.teneted.taiyitist.injection.world.entity.monster;

import net.minecraft.world.entity.Entity;

public interface InjectionCreeper {

    default Entity bridge$entityIgniter() {
        throw new AssertionError("Not implemented");
    }

    default void taiyitist$setEntityIgniter(Entity entityIgniter) {
        throw new AssertionError("Not implemented");
    }

    default void setPowered(boolean power) {
        throw new AssertionError("Not implemented");
    }
}
