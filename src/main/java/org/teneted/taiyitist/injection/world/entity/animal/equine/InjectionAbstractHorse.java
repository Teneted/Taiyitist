package org.teneted.taiyitist.injection.world.entity.animal.equine;

public interface InjectionAbstractHorse {

    default int bridge$maxDomestication() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setMaxDomestication(int maxDomestication) {
        throw new IllegalStateException("Not implemented");
    }
}
