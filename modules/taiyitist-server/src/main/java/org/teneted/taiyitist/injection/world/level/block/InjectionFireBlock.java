package org.teneted.taiyitist.injection.world.level.block;

public interface InjectionFireBlock {

    default boolean bridge$canBurn(net.minecraft.world.level.block.Block block) {
        return false;
    }
}
