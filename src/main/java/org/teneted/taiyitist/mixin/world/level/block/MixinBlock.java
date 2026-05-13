package org.teneted.taiyitist.mixin.world.level.block;

import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.block.InjectionBlock;

@Mixin(Block.class)
public class MixinBlock implements InjectionBlock {
}
