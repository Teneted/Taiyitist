package org.teneted.taiyitist.mixin.server.players;

import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.server.players.InjectionPlayerList;

@Mixin(PlayerList.class)
public class MixinPlayerList implements InjectionPlayerList {
}
