package org.teneted.taiyitist.mixin.world.level;

import net.minecraft.world.level.LevelWriter;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.InjectionLevelWriter;

@Mixin(LevelWriter.class)
public class MixinLevelWriter implements InjectionLevelWriter {
}
