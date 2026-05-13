package org.teneted.taiyitist.mixin.world.level.block.entity;

import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.block.entity.InjectionShulkerBoxBlockEntity;

@Mixin(ShulkerBoxBlockEntity.class)
public class MixinShulkerBoxBlockEntity implements InjectionShulkerBoxBlockEntity {
}
