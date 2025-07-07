package com.taiyitistmc.injection.world.entity.npc;

public interface InjectionVillager {

    default long bridge$gossipDecayInterval() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setGossipDecayInterval(long gossipDecayInterval) {
        throw new IllegalStateException("Not implemented");
    }
}
