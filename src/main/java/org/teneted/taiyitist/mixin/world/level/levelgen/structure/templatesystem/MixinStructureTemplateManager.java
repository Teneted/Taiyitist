package org.teneted.taiyitist.mixin.world.level.levelgen.structure.templatesystem;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.levelgen.structure.templatesystem.InjectionStructureTemplateManager;

@Mixin(StructureTemplateManager.class)
public class MixinStructureTemplateManager implements InjectionStructureTemplateManager {
}
