package org.teneted.taiyitist.mixin.world.entity.player;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.entity.player.InjectionPlayer_BedSleepingProblem;

@Mixin(Player.BedSleepingProblem.class)
public class MixinPlayer_BedSleepingProblem implements InjectionPlayer_BedSleepingProblem {
}
