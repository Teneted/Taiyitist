package org.celestial_artistry.taiyitist.injection.network.protocol.game;

public interface InjectionServerboundUseItemPacket {

    default long bridge$timestamp() {
        return 0;
    }
}
