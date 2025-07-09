package com.taiyitistmc.injection.network.syncher;

import net.minecraft.network.syncher.EntityDataAccessor;

public interface InjectionSynchedEntityData {

    default <T> void markDirty(EntityDataAccessor<T> datawatcherobject) {
        throw new IllegalStateException("Not implemented");
    }
}
