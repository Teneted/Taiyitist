package com.taiyitistmc.mixin.world.level.entity;

import com.taiyitistmc.injection.world.level.entity.InjectionEntityAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityAccess.class)
public interface MixinEntityAccess extends InjectionEntityAccess {

    @Shadow void setRemoved(Entity.RemovalReason removalReason);

    // CraftBukkit start - add Bukkit remove cause
    @Override
    default void setRemoved(Entity.RemovalReason entity_removalreason, EntityRemoveEvent.Cause cause) {
        setRemoved(entity_removalreason);
    }
    // CraftBukkit end
}
