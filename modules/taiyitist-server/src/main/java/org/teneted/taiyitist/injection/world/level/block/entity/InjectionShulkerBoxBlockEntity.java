package org.teneted.taiyitist.injection.world.level.block.entity;

import java.util.List;
import org.bukkit.entity.HumanEntity;

public interface InjectionShulkerBoxBlockEntity {

    default List<HumanEntity> bridge$transaction() {
        return null;
    }

    default void taiyitist$setTransaction(List<HumanEntity> transaction) {
    }

    default boolean bridge$opened() {
        return false;
    }

    default void taiyitist$setOpened(boolean opened) {
    }
}
