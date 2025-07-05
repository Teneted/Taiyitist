package com.taiyitistmc.injection.world.entity.projectile;

import net.minecraft.world.item.Item;

public interface InjectionThrowableItemProjectile {

    default Item getDefaultItemPublic() {
        return null;
    }
}
