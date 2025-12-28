package org.teneted.taiyitist.injection.world.entity;

import net.minecraft.world.entity.monster.Guardian;

public interface InjectionGuardian {

    default Guardian.GuardianAttackGoal bridge$guardianAttackGoal() {
        return null;
    }

    default void taiyitist$setGuardianAttackGoal(Guardian.GuardianAttackGoal guardianAttackGoal) {
    }
}
