package org.teneted.taiyitist.mixin.server.level;

import net.minecraft.server.level.ServerChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.server.level.InjectionServerChunkCache;

@Mixin(ServerChunkCache.class)
public class MixinServerChunkCache implements InjectionServerChunkCache {
}
