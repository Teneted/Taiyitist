package org.teneted.taiyitist.mixin.world.level.gamerules;

import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.gamerules.InjectionGameRules;

@Mixin(GameRules.class)
public class MixinGameRules implements InjectionGameRules {
}
