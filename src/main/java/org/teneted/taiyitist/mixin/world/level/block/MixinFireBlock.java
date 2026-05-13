package org.teneted.taiyitist.mixin.world.level.block;

import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.block.InjectionFireBlock;

@Mixin(FireBlock.class)
public class MixinFireBlock implements InjectionFireBlock {
}
