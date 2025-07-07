package com.taiyitistmc.injection.world.level.saveddata.maps;

import java.util.UUID;
import org.bukkit.craftbukkit.v1_20_R1.map.CraftMapView;

public interface InjectionMapItemSavedData {

    default CraftMapView bridge$mapView() {
        return null;
    }

    default UUID bridge$uniqueId() {
        return null;
    }

    default void taiyitist$setUniqueId(UUID uuid) {
    }

    default String bridge$id() {
        return null;
    }

    default void taiyitist$setId(String id) {
    }
}
