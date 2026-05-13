package org.teneted.taiyitist.mixin.world.level.levelgen.structure.templatesystem;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.levelgen.structure.templatesystem.InjectionStructureTemplate;

@Mixin(StructureTemplate.class)
public class MixinStructureTemplate implements InjectionStructureTemplate {
}
