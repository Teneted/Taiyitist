package org.teneted.taiyitist.mixin.world.level.storage;

import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.storage.InjectionLevelStorageSource;

@Mixin(LevelStorageSource.LevelStorageAccess.class)
public class MixinLevelStorageAccess implements InjectionLevelStorageSource {
}
