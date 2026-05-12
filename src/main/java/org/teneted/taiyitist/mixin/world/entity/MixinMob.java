package org.teneted.taiyitist.mixin.world.entity;

import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.InjectionMob;

@Mixin(Mob.class)
public class MixinMob implements InjectionMob {
}
