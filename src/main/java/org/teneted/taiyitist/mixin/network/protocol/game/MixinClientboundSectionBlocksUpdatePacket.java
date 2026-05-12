package org.teneted.taiyitist.mixin.network.protocol.game;

import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.network.protocol.game.InjectionClientboundSectionBlocksUpdatePacket;

@Mixin(ClientboundSectionBlocksUpdatePacket.class)
public class MixinClientboundSectionBlocksUpdatePacket implements InjectionClientboundSectionBlocksUpdatePacket {
}
