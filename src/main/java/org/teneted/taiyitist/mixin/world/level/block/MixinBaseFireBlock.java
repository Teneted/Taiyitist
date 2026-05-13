package org.teneted.taiyitist.mixin.world.level.block;

import net.minecraft.world.level.block.BaseFireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.block.InjectionBaseFireBlock;

@Mixin(BaseFireBlock.class)
public class MixinBaseFireBlock implements InjectionBaseFireBlock {
}
