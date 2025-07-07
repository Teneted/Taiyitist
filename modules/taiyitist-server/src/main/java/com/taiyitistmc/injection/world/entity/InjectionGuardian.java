package com.taiyitistmc.injection.world.entity;

import net.minecraft.world.entity.monster.Guardian;

public interface InjectionGuardian {

    default Guardian.GuardianAttackGoal bridge$guardianAttackGoal() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setGuardianAttackGoal(Guardian.GuardianAttackGoal guardianAttackGoal) {
        throw new IllegalStateException("Not implemented");
    }
}
