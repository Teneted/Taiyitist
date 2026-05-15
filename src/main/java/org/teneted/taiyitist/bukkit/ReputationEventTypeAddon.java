package org.teneted.taiyitist.bukkit;

import net.minecraft.world.entity.ai.village.ReputationEventType;

public interface ReputationEventTypeAddon {

    java.util.Map<String, ReputationEventType> BY_ID = com.google.common.collect.Maps.newHashMap(); // CraftBukkit - map with all values

    // CraftBukkit start - additional events added in the API
    ReputationEventType GOSSIP = ReputationEventType.register("bukkit_gossip");
    ReputationEventType DECAY = ReputationEventType.register("bukkit_decay");
    ReputationEventType UNSPECIFIED = ReputationEventType.register("bukkit_unspecified");
    // CraftBukkit end
}
