package org.teneted.taiyitist.injection.world.entity.monster.piglin;

import java.util.Set;
import net.minecraft.world.item.Item;

public interface InjectionPiglin {

    default Set<Item> bridge$allowedBarterItems() {
        throw new IllegalStateException("Not implemented");
    }

    default Set<Item> bridge$interestItems() {
        throw new IllegalStateException("Not implemented");
    }
}
