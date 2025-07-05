package com.taiyitistmc.injection.server.level;

import java.util.Set;
import net.minecraft.server.network.ServerPlayerConnection;

public interface InjectionServerEntity {

    default void setTrackedPlayers(Set<ServerPlayerConnection> trackedPlayers) {
    }
}
