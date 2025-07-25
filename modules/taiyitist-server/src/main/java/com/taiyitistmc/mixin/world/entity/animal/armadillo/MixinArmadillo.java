package com.taiyitistmc.mixin.world.entity.animal.armadillo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Armadillo.class)
public abstract class MixinArmadillo extends Animal {

    protected MixinArmadillo(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/armadillo/Armadillo;dropFromGiftLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/resources/ResourceKey;Ljava/util/function/BiConsumer;)Z"))
    private void taiyitist$setForceDropsTrue(ServerLevel serverLevel, CallbackInfo ci) {
        this.taiyitist$setForceDrops(true);
    }

    @Inject(method = "customServerAiStep", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/animal/armadillo/Armadillo;scuteTime:I", ordinal = 2))
    private void taiyitist$setForceDropsFalse(ServerLevel serverLevel, CallbackInfo ci) {
        this.taiyitist$setForceDrops(false);
    }

    @Inject(method = "brushOffScute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/armadillo/Armadillo;spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private void taiyitist$setForceDropsTrue0(CallbackInfoReturnable<Boolean> cir) {
        this.taiyitist$setForceDrops(true);
    }

    @Inject(method = "brushOffScute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/armadillo/Armadillo;gameEvent(Lnet/minecraft/core/Holder;)V"))
    private void taiyitist$setForceDropsFalse0(CallbackInfoReturnable<Boolean> cir) {
        this.taiyitist$setForceDrops(false);
    }
}
