package org.teneted.taiyitist.mixin.network.protocol.game;

import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.network.protocol.game.InjectionServerboundUseItemOnPacket;

@Mixin(ServerboundUseItemOnPacket.class)
public class MixinServerboundUseItemOnPacket implements InjectionServerboundUseItemOnPacket {
}
