package com.taiyitistmc.injection.network.protocol.game;

public interface InjectionServerboundUseItemPacket {

    default long bridge$timestamp() {
        return 0;
    }
}
