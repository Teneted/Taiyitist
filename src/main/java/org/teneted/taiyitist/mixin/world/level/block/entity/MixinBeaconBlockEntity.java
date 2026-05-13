package org.teneted.taiyitist.mixin.world.level.block.entity;

import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.block.entity.InjectionBeaconBlockEntity;

@Mixin(BeaconBlockEntity.class)
public class MixinBeaconBlockEntity implements InjectionBeaconBlockEntity {
}
