package org.teneted.taiyitist.mixin.world.level;

import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.InjectionLevel;

@Mixin(Level.class)
public class MixinLevel implements InjectionLevel {
}
