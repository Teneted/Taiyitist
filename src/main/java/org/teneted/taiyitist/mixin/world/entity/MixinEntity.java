package org.teneted.taiyitist.mixin.world.entity;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.InjectionEntity;

@Mixin(Entity.class)
public class MixinEntity implements InjectionEntity {
}
