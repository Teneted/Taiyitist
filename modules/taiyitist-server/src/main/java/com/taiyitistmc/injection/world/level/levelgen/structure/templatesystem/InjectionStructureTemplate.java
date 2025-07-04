package com.taiyitistmc.injection.world.level.levelgen.structure.templatesystem;

import org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer;

public interface InjectionStructureTemplate {

    default CraftPersistentDataContainer bridge$persistentDataContainer() {
        throw new IllegalStateException("Not implemented");
    }
}
