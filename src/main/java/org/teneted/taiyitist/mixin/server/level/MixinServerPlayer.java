package org.teneted.taiyitist.mixin.server.level;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.server.level.InjectionServerPlayer;

@Mixin(ServerPlayer.class)
public class MixinServerPlayer implements InjectionServerPlayer {
}
