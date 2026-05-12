package org.teneted.taiyitist.mixin.world.entity;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.InjectionLivingEntity;

@Mixin(LivingEntity.class)
public class MixinLivingEntity implements InjectionLivingEntity {
}
