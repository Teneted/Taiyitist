package com.taiyitistmc.injection.advancements;

public interface InjectionAdvancement {

    default org.bukkit.advancement.Advancement bridge$bukkit() {
        return null;
    }
}
