package com.taiyitistmc.mixin.world.level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CollisionContext.class)
public interface MixinClipContext {

    @Inject(method = "of(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/shapes/CollisionContext;", at = @At(value = "HEAD"), cancellable = true)
    private static void taiyitist$modifyArgs(Entity entity, CallbackInfoReturnable<CollisionContext> cir) {
        if (entity == null) {
            cir.setReturnValue(CollisionContext.empty());
        }
    }
}
