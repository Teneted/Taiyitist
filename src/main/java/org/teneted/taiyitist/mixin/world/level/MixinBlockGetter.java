package org.teneted.taiyitist.mixin.world.level;

import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.InjectionBlockGetter;

@Mixin(BlockGetter.class)
public class MixinBlockGetter implements InjectionBlockGetter {
}
