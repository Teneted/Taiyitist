package org.teneted.taiyitist.injection.world.level.portal;

import net.minecraft.world.entity.Entity;

public interface InjectionPortalForcer {

    default void pushPortalCreate(Entity entity, int createRadius) {

    }

    default void pushSearchRadius(int searchRadius) {

    }
}
