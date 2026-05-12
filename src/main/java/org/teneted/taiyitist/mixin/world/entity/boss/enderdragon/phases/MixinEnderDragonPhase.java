package org.teneted.taiyitist.mixin.world.entity.boss.enderdragon.phases;

import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.boss.enderdragon.phases.InjectionEnderDragonPhase;

@Mixin(EnderDragonPhase.class)
public class MixinEnderDragonPhase implements InjectionEnderDragonPhase {
}
