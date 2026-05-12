package org.teneted.taiyitist.mixin.world.entity.monster;

import net.minecraft.world.entity.monster.Guardian;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.monster.InjectionGuardian;

@Mixin(Guardian.class)
public class MixinGuardian implements InjectionGuardian {
}
