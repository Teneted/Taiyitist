package org.teneted.taiyitist.mixin.world.level;

import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.InjectionServerLevelAccessor;

@Mixin(ServerLevelAccessor.class)
public interface MixinServerLevelAccessor extends InjectionServerLevelAccessor {
}
