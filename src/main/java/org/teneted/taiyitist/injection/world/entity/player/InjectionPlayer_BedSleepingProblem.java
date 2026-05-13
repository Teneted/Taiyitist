package org.teneted.taiyitist.injection.world.entity.player;

import org.bukkit.event.player.PlayerBedEnterEvent;

public interface InjectionPlayer_BedSleepingProblem {

    default PlayerBedEnterEvent.BedEnterResult bukkit() {
        throw new AssertionError("Not implemented");
    }
}
