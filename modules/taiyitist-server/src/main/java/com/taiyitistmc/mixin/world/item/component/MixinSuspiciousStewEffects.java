package com.taiyitistmc.mixin.world.item.component;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ConsumableListener;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.item.component.TooltipProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(SuspiciousStewEffects.class)
public abstract class MixinSuspiciousStewEffects implements ConsumableListener, TooltipProvider {

    @Shadow @Final private List<SuspiciousStewEffects.Entry> effects;

    @Override
    public void cancelUsingItem(ServerPlayer entityplayer, ItemStack itemstack) {
        for (SuspiciousStewEffects.Entry suspicioussteweffects_a : this.effects) {
            entityplayer.connection.send(new net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket(entityplayer.getId(), suspicioussteweffects_a.effect()));
        }
    }
}
