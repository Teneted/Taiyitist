package org.teneted.taiyitist.mixin.world.damagesource;

import net.minecraft.world.damagesource.CombatTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.damagesource.InjectionCombatTracker;

@Mixin(CombatTracker.class)
public class MixinCombatTracker implements InjectionCombatTracker {
}
