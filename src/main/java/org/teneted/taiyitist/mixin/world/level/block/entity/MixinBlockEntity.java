package org.teneted.taiyitist.mixin.world.level.block.entity;

import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.block.entity.InjectionBlockEntity;

@Mixin(BlockEntity.class)
public class MixinBlockEntity implements InjectionBlockEntity {
}
