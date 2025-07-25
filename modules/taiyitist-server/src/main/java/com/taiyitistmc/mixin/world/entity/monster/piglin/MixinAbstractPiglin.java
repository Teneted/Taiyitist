package com.taiyitistmc.mixin.world.entity.monster.piglin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractPiglin.class)
public abstract class MixinAbstractPiglin extends Monster {

    protected MixinAbstractPiglin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "finishConversion", at = @At("HEAD"))
    private void taiyitist$pushPiglinCause(ServerLevel serverLevel, CallbackInfo ci) {
        this.bridge$pushTransformReason(EntityTransformEvent.TransformReason.PIGLIN_ZOMBIFIED);
        serverLevel.pushAddEntityReason(CreatureSpawnEvent.SpawnReason.PIGLIN_ZOMBIFIED);
    }
}
