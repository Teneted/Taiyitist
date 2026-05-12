package org.teneted.taiyitist.injection.world.entity.projectile.throwableitemprojectile;

import net.minecraft.world.item.Item;

public interface InjectionThrowableItemProjectile {

    default Item getDefaultItemPublic() {
        throw new IllegalStateException("Not implemented");
    }
}
