package org.celestial_artistry.taiyitist.injection.advancements;

public interface InjectionAdvancement {

    default org.bukkit.advancement.Advancement bridge$bukkit() {
        return null;
    }
}
