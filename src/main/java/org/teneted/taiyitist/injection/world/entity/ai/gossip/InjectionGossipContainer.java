package org.teneted.taiyitist.injection.world.entity.ai.gossip;

import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.npc.villager.Villager;

import java.util.UUID;
import java.util.function.Predicate;

public interface InjectionGossipContainer {

    default int getReputation(UUID entity, Predicate<GossipType> types, boolean weighted) {
        throw new AssertionError("Not implemented");
    }

    default void add(UUID target, GossipType type, int amountToAdd, org.bukkit.entity.Villager.ReputationEvent changeReason) {
        throw new AssertionError("Not implemented");
    }

    default void set(UUID target, GossipType type, int amount, org.bukkit.entity.Villager.ReputationEvent changeReason) {
        throw new AssertionError("Not implemented");
    }

    default void remove(UUID target, GossipType type, int amountToRemove, org.bukkit.entity.Villager.ReputationEvent changeReason) {
        throw new AssertionError("Not implemented");
    }

    default void remove(UUID target, GossipType type, org.bukkit.entity.Villager.ReputationEvent changeReason) {
        throw new AssertionError("Not implemented");
    }

    default void remove(GossipType type, org.bukkit.entity.Villager.ReputationEvent changeReason) {
        throw new AssertionError("Not implemented");
    }

    default void decay(Villager villager, UUID uuid) {
        throw new AssertionError("Not implemented");
    }

    default int unweightedValue(Predicate<GossipType> predicate) {
        throw new AssertionError("Not implemented");
    }
}
