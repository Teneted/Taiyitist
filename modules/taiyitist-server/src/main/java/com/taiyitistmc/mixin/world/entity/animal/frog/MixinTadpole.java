package com.taiyitistmc.mixin.world.entity.animal.frog;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Tadpole.class)
public abstract class MixinTadpole extends AbstractFish {

    public MixinTadpole(EntityType<? extends AbstractFish> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "ageUp()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/frog/Tadpole;convertTo(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/ConversionParams;Lnet/minecraft/world/entity/ConversionParams$AfterConversion;)Lnet/minecraft/world/entity/Mob;"))
    private void taiyitist$pushReasons(CallbackInfo ci) {
        this.bridge$pushTransformReason(EntityTransformEvent.TransformReason.METAMORPHOSIS);
        this.level().pushAddEntityReason(CreatureSpawnEvent.SpawnReason.METAMORPHOSIS);
    }
}
