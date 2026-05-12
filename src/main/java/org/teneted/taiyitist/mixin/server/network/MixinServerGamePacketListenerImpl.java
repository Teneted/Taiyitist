package org.teneted.taiyitist.mixin.server.network;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.server.network.InjectionServerGamePacketListenerImpl;

@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerGamePacketListenerImpl implements InjectionServerGamePacketListenerImpl {
}
