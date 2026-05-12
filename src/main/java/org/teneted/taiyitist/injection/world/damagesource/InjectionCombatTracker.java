package org.teneted.taiyitist.injection.world.damagesource;

import net.minecraft.network.chat.Component;

public interface InjectionCombatTracker {

    default void taiyitist$setDeathMessage(Component component) {
        throw new IllegalStateException("Not implemented");
    }
}
