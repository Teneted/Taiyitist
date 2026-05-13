package org.teneted.taiyitist.mixin.world.level.chunk;

import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.chunk.InjectionChunkAccess;

@Mixin(ChunkAccess.class)
public class MixinChunkAccess implements InjectionChunkAccess {
}
