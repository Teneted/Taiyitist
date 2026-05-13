package org.teneted.taiyitist.mixin.world.level.block.entity;

import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.block.entity.InjectionFurnaceBlockEntity;

@Mixin(FurnaceBlockEntity.class)
public class MixinFurnaceBlockEntity implements InjectionFurnaceBlockEntity {
}
