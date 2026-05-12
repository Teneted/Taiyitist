package org.teneted.taiyitist.injection.network.protocol.game;

import net.minecraft.world.level.block.state.BlockState;

public interface InjectionClientboundSectionBlocksUpdatePacket {

    default void putBukkitPacket(BlockState[] states) {
        throw new IllegalStateException("Not implemented");
    }
}
