package org.teneted.taiyitist.injection.world.level.levelgen.structure.templatesystem;

import java.util.Optional;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public interface InjectionStructureTemplateManager {

    default Optional<StructureTemplate> loadFromResource0(Identifier id) {
        return Optional.empty();
    }
}
