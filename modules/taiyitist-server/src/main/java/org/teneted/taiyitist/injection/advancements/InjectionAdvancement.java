package org.teneted.taiyitist.injection.advancements;

public interface InjectionAdvancement {

    default org.bukkit.advancement.Advancement bridge$bukkit() {
        return null;
    }
}
