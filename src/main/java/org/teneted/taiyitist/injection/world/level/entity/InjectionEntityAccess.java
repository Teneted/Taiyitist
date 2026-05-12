package org.teneted.taiyitist.injection.world.level.entity;

import net.minecraft.world.entity.Entity;
import org.bukkit.event.entity.EntityRemoveEvent;

public interface InjectionEntityAccess {

    default void setRemoved(Entity.RemovalReason entity_removalreason, EntityRemoveEvent.Cause cause) {
        throw new IllegalStateException("Not implemented");
    }
}
