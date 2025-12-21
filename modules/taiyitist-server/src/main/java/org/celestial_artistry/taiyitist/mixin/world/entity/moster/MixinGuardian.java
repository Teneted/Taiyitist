package org.celestial_artistry.taiyitist.mixin.world.entity.moster;

import org.celestial_artistry.taiyitist.injection.world.entity.InjectionGuardian;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Guardian;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Guardian.class)
public class MixinGuardian implements InjectionGuardian {

    @Unique
    public Guardian.GuardianAttackGoal guardianAttackGoal;

    @ModifyArg(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V"))
    private Goal taiyitist$saveGoal(Goal goal) {
        if (goal instanceof Guardian.GuardianAttackGoal guardianGoal) {
            this.guardianAttackGoal = guardianGoal;
        }
        return goal;
    }

    @Override
    public Guardian.GuardianAttackGoal bridge$guardianAttackGoal() {
        return guardianAttackGoal;
    }

    @Override
    public void taiyitist$setGuardianAttackGoal(Guardian.GuardianAttackGoal guardianAttackGoal) {
        this.guardianAttackGoal = guardianAttackGoal;
    }
}
