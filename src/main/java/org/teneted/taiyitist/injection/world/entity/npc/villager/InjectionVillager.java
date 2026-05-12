package org.teneted.taiyitist.injection.world.entity.npc.villager;

public interface InjectionVillager {

    default long bridge$gossipDecayInterval() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setGossipDecayInterval(long gossipDecayInterval) {
        throw new IllegalStateException("Not implemented");
    }
}
