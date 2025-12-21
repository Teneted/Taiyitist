package org.celestial_artistry.taiyitist.injection.world.entity;

public interface InjectionAbstractHorse {

    default int bridge$maxDomestication() {
        return 0;
    }

    default void taiyitist$setMaxDomestication(int maxDomestication) {

    }
}
