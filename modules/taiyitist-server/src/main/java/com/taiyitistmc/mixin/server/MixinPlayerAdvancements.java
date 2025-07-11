package com.taiyitistmc.mixin.server;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancements.class)
public class MixinPlayerAdvancements {

    @Shadow private ServerPlayer player;

    @Inject(method = "method_53639", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), cancellable = true)
    private void taiyitist$checkVanilla(ServerAdvancementManager serverAdvancementManager, ResourceLocation resourceLocation, AdvancementProgress advancementProgress, CallbackInfo ci) {
        if (!resourceLocation.getNamespace().equals("minecraft")) ci.cancel(); // CraftBukkit
    }

    @Inject(method = "award", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementRewards;grant(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void taiyitist$handlePlayerAdvancementDoneEvent(AdvancementHolder advancementHolder, String string, CallbackInfoReturnable<Boolean> cir) {
        this.player.level().getCraftServer().getPluginManager().callEvent(new PlayerAdvancementDoneEvent(this.player.getBukkitEntity(), advancementHolder.toBukkit())); // CraftBukkit
    }
}
