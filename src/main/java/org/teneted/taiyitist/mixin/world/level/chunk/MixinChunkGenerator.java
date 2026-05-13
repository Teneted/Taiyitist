package org.teneted.taiyitist.mixin.world.level.chunk;

import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.chunk.InjectionChunkGenerator;

@Mixin(ChunkGenerator.class)
public class MixinChunkGenerator implements InjectionChunkGenerator {
}
