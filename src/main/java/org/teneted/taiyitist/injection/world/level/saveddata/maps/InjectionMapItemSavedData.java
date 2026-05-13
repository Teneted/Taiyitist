package org.teneted.taiyitist.injection.world.level.saveddata.maps;

import java.util.UUID;

import net.minecraft.world.level.saveddata.maps.MapId;
import org.bukkit.craftbukkit.map.CraftMapView;

public interface InjectionMapItemSavedData {

    default CraftMapView bridge$mapView() {
        throw new IllegalStateException("Not implemented");
    }

    default UUID bridge$uniqueId() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setUniqueId(UUID uuid) {
        throw new IllegalStateException("Not implemented");
    }

    default MapId bridge$id() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setId(MapId id) {
        throw new IllegalStateException("Not implemented");
    }
}
