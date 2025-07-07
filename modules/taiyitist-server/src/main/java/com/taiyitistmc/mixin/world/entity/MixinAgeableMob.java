package com.taiyitistmc.mixin.world.entity;

import com.taiyitistmc.injection.world.entity.InjectionAgeableMob;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AgeableMob.class)
public abstract class MixinAgeableMob extends PathfinderMob implements InjectionAgeableMob {

    @Unique
    public boolean ageLocked; // CraftBukkit

    protected MixinAgeableMob(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void taiyitist$writeAgeLocked(CompoundTag compound, CallbackInfo ci) {
        compound.putBoolean("AgeLocked", ageLocked);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void taiyitist$readAgeLocked(CompoundTag compound, CallbackInfo ci) {
        ageLocked = compound.getBoolean("AgeLocked");
    }

    @Redirect(method = "aiStep", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;isClientSide:Z"))
    private boolean taiyitist$tickIfNotLocked(Level instance) {
        return this.level().isClientSide || ageLocked;
    }

    @Override
    public boolean bridge$ageLocked() {
        return ageLocked;
    }

    @Override
    public void taiyitist$setAgeLocked(boolean ageLocked) {
        this.ageLocked = ageLocked;
    }
}
