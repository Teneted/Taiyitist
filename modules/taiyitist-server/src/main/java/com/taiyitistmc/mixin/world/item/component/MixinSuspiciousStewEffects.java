package com.taiyitistmc.mixin.world.item.component;

import java.util.List;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SuspiciousStewEffects.class)
public class MixinSuspiciousStewEffects {

    @Shadow @Final private List<SuspiciousStewEffects.Entry> effects;

    // Banner start - support for some Paper plugins
    public void cancelUsingItem(net.minecraft.server.level.ServerPlayer player, ItemStack stack, List<Packet<? super ClientGamePacketListener>> collectedPackets) { // Paper - properly resend entities - collect packets for bundle
        for (SuspiciousStewEffects.Entry entry : this.effects) {
            collectedPackets.add(new net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket(player.getId(), entry.effect())); // Paper - bundlize packets
        }
    }
}
