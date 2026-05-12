package org.teneted.taiyitist.mixin.server.level;

import net.minecraft.server.level.ChunkHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.server.level.InjectionChunkHolder;

@Mixin(ChunkHolder.class)
public class MixinChunkHolder implements InjectionChunkHolder {
}
