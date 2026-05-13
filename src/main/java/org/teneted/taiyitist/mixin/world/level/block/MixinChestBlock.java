package org.teneted.taiyitist.mixin.world.level.block;

import net.minecraft.world.level.block.ChestBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.block.InjectionChestBlock;

@Mixin(ChestBlock.class)
public class MixinChestBlock implements InjectionChestBlock {
}
