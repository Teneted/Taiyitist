package org.teneted.taiyitist.mixin.world.level.border;

import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.border.InjectionWorldBorder;

@Mixin(WorldBorder.class)
public class MixinWorldBorder implements InjectionWorldBorder {
}
