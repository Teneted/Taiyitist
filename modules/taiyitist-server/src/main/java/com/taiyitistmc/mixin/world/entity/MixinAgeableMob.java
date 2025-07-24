package com.taiyitistmc.mixin.world.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.taiyitistmc.injection.world.entity.InjectionAgeableMob;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AgeableMob.class)
public abstract class MixinAgeableMob  extends PathfinderMob implements InjectionAgeableMob {

    @Shadow public abstract int getAge();

    @Shadow public abstract void setAge(int i);

    public boolean ageLocked; // CraftBukkit

    protected MixinAgeableMob(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean bridge$ageLocked() {
        return this.ageLocked;
    }

    @Override
    public void taiyitist$setAgeLocked(boolean ageLocked) {
        this.ageLocked = ageLocked;
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void taiyitist$readLocked(ValueInput valueInput, CallbackInfo ci) {
        this.ageLocked = valueInput.getBooleanOr("AgeLocked", this.ageLocked); // CraftBukkit
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void taiyitist$putLocked(ValueOutput valueOutput, CallbackInfo ci) {
        valueOutput.putBoolean("AgeLocked", this.ageLocked); // CraftBukkit
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;isClientSide:Z"))
    private boolean taiyitist$checkLocked(boolean original) {
        return original || ageLocked;
    }

    @Override
    public void inactiveTick() {
        super.inactiveTick();
        if ( this.level().isClientSide || this.ageLocked ) {
            this.refreshDimensions();
        } else {
            int i = this.getAge();
            if ( i < 0 ) {
                ++i;
                this.setAge( i );
            } else if ( i > 0 ) {
                --i;
                this.setAge( i );
            }
        }
    }
}
