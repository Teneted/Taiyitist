package org.teneted.taiyitist.mixin.server.level;

import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.server.level.InjectionServerPlayerGameMode;

@Mixin(ServerPlayerGameMode.class)
public class MixinServerPlayerGameMode implements InjectionServerPlayerGameMode {
}
