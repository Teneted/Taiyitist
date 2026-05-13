package org.teneted.taiyitist.injection.world.level.portal;

import org.bukkit.event.player.PlayerTeleportEvent;

public interface InjectionTeleportTransition {

    default void taiyitist$setTeleportCause(PlayerTeleportEvent.TeleportCause cause) {
        throw new AssertionError("Not implemented");
    }

    default PlayerTeleportEvent.TeleportCause bridge$teleportCause() {
        throw new AssertionError("Not implemented");
    }
}
