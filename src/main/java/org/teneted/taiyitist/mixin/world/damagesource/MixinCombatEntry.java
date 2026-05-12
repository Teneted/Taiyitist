package org.teneted.taiyitist.mixin.world.damagesource;

import net.minecraft.world.damagesource.CombatEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.damagesource.InjectionCombatEntry;

@Mixin(CombatEntry.class)
public class MixinCombatEntry implements InjectionCombatEntry {
}
