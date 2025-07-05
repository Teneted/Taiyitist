package com.taiyitistmc.injection.world.entity;

public interface InjectionAbstractHorse {

    default int bridge$maxDomestication() {
        return 0;
    }

    default void banner$setMaxDomestication(int maxDomestication) {

    }
}
