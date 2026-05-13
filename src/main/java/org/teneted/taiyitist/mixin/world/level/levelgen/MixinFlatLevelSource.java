package org.teneted.taiyitist.mixin.world.level.levelgen;

import net.minecraft.world.level.levelgen.FlatLevelSource;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.levelgen.InjectionFlatLevelSource;

@Mixin(FlatLevelSource.class)
public class MixinFlatLevelSource implements InjectionFlatLevelSource {
}
