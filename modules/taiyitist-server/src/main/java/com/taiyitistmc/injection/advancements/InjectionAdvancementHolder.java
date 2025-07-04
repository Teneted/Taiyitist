package com.taiyitistmc.injection.advancements;

public interface InjectionAdvancementHolder {

    default org.bukkit.advancement.Advancement toBukkit() {
        throw new IllegalStateException("Not implemented");
    }
}
