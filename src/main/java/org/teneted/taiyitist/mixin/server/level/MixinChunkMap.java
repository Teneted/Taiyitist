package org.teneted.taiyitist.mixin.server.level;

import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.server.level.InjectionChunkMap;

@Mixin(ChunkMap.class)
public class MixinChunkMap implements InjectionChunkMap {
}
