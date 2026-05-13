package org.teneted.taiyitist.mixin.world.level.chunk;

import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.chunk.InjectionLevelChunkSection;

@Mixin(LevelChunkSection.class)
public class MixinLevelChunkSection implements InjectionLevelChunkSection {
}
