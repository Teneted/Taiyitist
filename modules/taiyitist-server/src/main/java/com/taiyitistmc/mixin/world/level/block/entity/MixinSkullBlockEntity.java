package com.taiyitistmc.mixin.world.level.block.entity;

import com.taiyitistmc.config.BannerConfig;
import com.mojang.authlib.GameProfile;
import java.util.function.Consumer;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkullBlockEntity.class)
public class MixinSkullBlockEntity {

   @Inject(method = "updateOwnerProfile", at = @At("HEAD"), cancellable = true)
   private void banner$updateOwnerProfile(CallbackInfo ci) {
      if (BannerConfig.disable_skullblock_skin) {
         ci.cancel();
      }
   }

   @Inject(method = "updateGameprofile", at = @At("HEAD"), cancellable = true)
   private static void banner$updateGameprofile(GameProfile profile, Consumer<GameProfile> profileConsumer, CallbackInfo ci) {
      if (BannerConfig.disable_skullblock_skin) {
         ci.cancel();
      }
   }
}
