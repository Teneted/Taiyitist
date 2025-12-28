package org.teneted.taiyitist.injection.world.level.border;

import net.minecraft.world.level.Level;

public interface InjectionWorldBorder {

    default Level bridge$world() {
        return null;
    }

    default void taiyitist$setWorld(Level world) {
    }
}
