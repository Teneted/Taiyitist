package org.teneted.taiyitist.mixin.world.level.block.entity;

import net.minecraft.world.level.block.entity.SculkCatalystBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.block.entity.InjectionCatalystListener;

@Mixin(SculkCatalystBlockEntity.CatalystListener.class)
public class MixinCatalystListener implements InjectionCatalystListener {
}
