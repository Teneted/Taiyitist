package org.teneted.taiyitist.mixin.network;

import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.network.InjectionConnection;

@Mixin(Connection.class)
public class MixinConnection implements InjectionConnection {
}
