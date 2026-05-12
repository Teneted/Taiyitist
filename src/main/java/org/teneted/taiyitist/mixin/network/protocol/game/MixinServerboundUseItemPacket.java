package org.teneted.taiyitist.mixin.network.protocol.game;

import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.network.protocol.game.InjectionServerboundUseItemPacket;

@Mixin(ServerboundUseItemPacket.class)
public class MixinServerboundUseItemPacket implements InjectionServerboundUseItemPacket {
}
