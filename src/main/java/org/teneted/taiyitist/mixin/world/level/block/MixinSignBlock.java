package org.teneted.taiyitist.mixin.world.level.block;

import net.minecraft.world.level.block.SignBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.block.InjectionSignBlock;

@Mixin(SignBlock.class)
public class MixinSignBlock implements InjectionSignBlock {
}
