package com.taiyitistmc.mixin.world.entity.ai.village;

import com.google.common.collect.Maps;
import net.minecraft.world.entity.ai.village.ReputationEventType;

import java.util.Map;

public interface ReputationEventTypeAddon {
    Map<String, ReputationEventType> BY_ID = Maps.newHashMap(); // CraftBukkit - map with all values
    // CraftBukkit start - additional events added in the API
    ReputationEventType GOSSIP = registerBukkit("bukkit_gossip");
    ReputationEventType DECAY = registerBukkit("bukkit_decay");
    ReputationEventType UNSPECIFIED = registerBukkit("bukkit_unspecified");
    // CraftBukkit end

    static ReputationEventType registerBukkit(final String string) {
        return new ReputationEventType() {
            public String toString() {
                return string;
            }
        };
    }
}
