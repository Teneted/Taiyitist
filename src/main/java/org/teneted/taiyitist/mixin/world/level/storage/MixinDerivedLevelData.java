package org.teneted.taiyitist.mixin.world.level.storage;

import net.minecraft.world.level.storage.DerivedLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.storage.InjectionDerivedLevelData;

@Mixin(DerivedLevelData.class)
public class MixinDerivedLevelData implements InjectionDerivedLevelData {
}
