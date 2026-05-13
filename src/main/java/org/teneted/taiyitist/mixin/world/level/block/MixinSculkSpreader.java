package org.teneted.taiyitist.mixin.world.level.block;

import net.minecraft.world.level.block.SculkSpreader;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.block.InjectionSculkSpreader;

@Mixin(SculkSpreader.class)
public class MixinSculkSpreader implements InjectionSculkSpreader {
}
