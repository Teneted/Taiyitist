package com.taiyitistmc.injection.world.level.portal;

import org.bukkit.event.player.PlayerTeleportEvent;

public interface InjectionDimensionTransition {

    default void setTeleportCause(PlayerTeleportEvent.TeleportCause cause) {

    }

    default PlayerTeleportEvent.TeleportCause getTeleportCause() {
        return PlayerTeleportEvent.TeleportCause.COMMAND;
    }
}
