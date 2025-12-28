package org.teneted.taiyitist.injection.server.bossevents;

import org.bukkit.boss.KeyedBossBar;

public interface InjectionCustomBossEvent {

    default KeyedBossBar getBukkitEntity() {
        return null;
    }
}
