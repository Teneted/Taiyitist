package org.teneted.taiyitist.injection.world.level.levelgen.structure;

public interface InjectionStructureStart {

    default org.bukkit.event.world.AsyncStructureGenerateEvent.Cause bridge$generationEventCause() {
        throw new IllegalStateException("Not implemented");
    }

    default org.bukkit.craftbukkit.persistence.DirtyCraftPersistentDataContainer bridge$persistentDataContainer() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setGenerationEventCause(org.bukkit.event.world.AsyncStructureGenerateEvent.Cause generationEventCause) {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setPersistentDataContainer(org.bukkit.craftbukkit.persistence.DirtyCraftPersistentDataContainer persistentDataContainer) {
        throw new IllegalStateException("Not implemented");
    }
}
