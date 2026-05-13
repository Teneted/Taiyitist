package org.teneted.taiyitist.mixin.world.level.chunk;

import net.minecraft.world.level.chunk.storage.RegionFileStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.chunk.storage.InjectionRegionFileStorage;

@Mixin(RegionFileStorage.class)
public class MixinRegionFileStorage implements InjectionRegionFileStorage {
}
