package org.teneted.taiyitist.injection.world.damagesource;

import net.minecraft.network.chat.Component;

public interface InjectionCombatEntry {

    default void taiyitist$setDeathMessage(Component component) {
        throw new IllegalStateException("Not implemented");
    }

    default Component bridge$deathMessage() {
        throw new IllegalStateException("Not implemented");
    }
}
