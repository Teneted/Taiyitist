package com.mohistmc.banner.mixin.world.item;

import com.mohistmc.banner.injection.world.item.InjectionSuspiciousStewItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(net.minecraft.world.item.SuspiciousStewItem.class)
public class MixinSuspiciousStewItem implements InjectionSuspiciousStewItem {

    // CraftBukkit start
    @Override
    public void cancelUsingItem(net.minecraft.server.level.ServerPlayer entityplayer, ItemStack itemstack) {
        SuspiciousStewEffects suspicioussteweffects = itemstack.getOrDefault(DataComponents.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffects.EMPTY);

        for (SuspiciousStewEffects.Entry suspicioussteweffects_a : suspicioussteweffects.effects()) {
            entityplayer.connection.send(new net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket(entityplayer.getId(), suspicioussteweffects_a.effect()));
        }
        entityplayer.server.getPlayerList().sendActivePlayerEffects(entityplayer);
    }
    // CraftBukkit end
}
