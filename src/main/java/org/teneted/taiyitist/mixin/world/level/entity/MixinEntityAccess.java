package org.teneted.taiyitist.mixin.world.level.entity;

import net.minecraft.world.level.entity.EntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.entity.InjectionEntityAccess;

@Mixin(EntityAccess.class)
public class MixinEntityAccess implements InjectionEntityAccess {
}
