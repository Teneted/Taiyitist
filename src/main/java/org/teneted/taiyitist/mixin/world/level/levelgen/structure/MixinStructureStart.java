package org.teneted.taiyitist.mixin.world.level.levelgen.structure;

import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.levelgen.structure.InjectionStructureStart;

@Mixin(StructureStart.class)
public class MixinStructureStart implements InjectionStructureStart {
}
