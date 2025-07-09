package com.taiyitistmc.mixin.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndPortalBlock.class)
public class MixinEndPortalBlock {

    @Inject(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;canUsePortal(Z)Z"), cancellable = true)
    public void taiyitist$enterPortal(BlockState blockState, Level level, BlockPos blockPos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier, CallbackInfo ci) {
        if (!Bukkit.getAllowEnd()) {
            if (entity instanceof Player player) {
                player.displayClientMessage(Component.literal("End dimension is not allow at this server"),
                        true);
            }
            ci.cancel();
        }
        EntityPortalEnterEvent event = new EntityPortalEnterEvent(entity.getBukkitEntity(),
                new Location(level.getWorld(), blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        new Location(level.getWorld(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
        Bukkit.getPluginManager().callEvent(event);
    }
}
