package org.teneted.taiyitist.injection.world.level.levelgen.structure.templatesystem;

import org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer;

public interface InjectionStructureTemplate {

    default CraftPersistentDataContainer bridge$persistentDataContainer() {
        throw new IllegalStateException("Not implemented");
    }
}
