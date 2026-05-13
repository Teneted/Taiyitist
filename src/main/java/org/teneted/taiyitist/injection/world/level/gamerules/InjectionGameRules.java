package org.teneted.taiyitist.injection.world.level.gamerules;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleMap;
import net.minecraft.world.level.gamerules.GameRules;
import org.jetbrains.annotations.Nullable;

public interface InjectionGameRules {

    default <T> void set(GameRule<T> gameRule, T value, @Nullable ServerLevel server) {
        throw new IllegalStateException("Not implemented");
    }

    default void setAll(GameRules other, @Nullable ServerLevel server) { // CraftBukkit - per-world
        throw new IllegalStateException("Not implemented");
    }

    default void setAll(GameRuleMap gameRulesMap, @Nullable ServerLevel server) {
        throw new IllegalStateException("Not implemented");
    }

    default <T> void setFromOther(GameRuleMap gameRulesMap, GameRule<T> gameRule, @Nullable ServerLevel server) {
        throw new IllegalStateException("Not implemented");
    }
}
