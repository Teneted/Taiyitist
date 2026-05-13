package org.teneted.taiyitist.mixin.world.level.chunk;

import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.chunk.InjectionLevelChunk;

@Mixin(LevelChunk.class)
public class MixinLevelChunk implements InjectionLevelChunk {
}
