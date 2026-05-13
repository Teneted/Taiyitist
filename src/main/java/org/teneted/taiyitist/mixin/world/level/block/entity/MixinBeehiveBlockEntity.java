package org.teneted.taiyitist.mixin.world.level.block.entity;

import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.block.entity.InjectionBeehiveBlockEntity;

@Mixin(BeehiveBlockEntity.class)
public class MixinBeehiveBlockEntity implements InjectionBeehiveBlockEntity {
}
