package org.teneted.taiyitist.mixin.world.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.InjectionEntityType;

@Mixin(EntityType.class)
public class MixinEntityType<T extends Entity> implements InjectionEntityType<T> {
}
