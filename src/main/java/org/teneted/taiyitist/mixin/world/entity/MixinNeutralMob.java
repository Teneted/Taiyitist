package org.teneted.taiyitist.mixin.world.entity;

import net.minecraft.world.entity.NeutralMob;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.InjectionNeutralMob;

@Mixin(NeutralMob.class)
public interface MixinNeutralMob extends InjectionNeutralMob {

}
