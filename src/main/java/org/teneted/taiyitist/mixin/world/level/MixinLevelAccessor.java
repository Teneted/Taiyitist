package org.teneted.taiyitist.mixin.world.level;

import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.InjectionLevelAccessor;

@Mixin(LevelAccessor.class)
public interface MixinLevelAccessor extends InjectionLevelAccessor {
}
