package org.teneted.taiyitist.injection.world.level.storage.loot;

import net.minecraft.world.Container;
import net.minecraft.world.level.storage.loot.LootParams;
import org.bukkit.craftbukkit.CraftLootTable;

public interface InjectionLootTable {

    default void fillInventory(Container iinventory, LootParams lootparams, long i, boolean plugin) {
        throw new IllegalStateException("Not implemented");
    }

    default CraftLootTable bridge$craftLootTable() {
        throw new IllegalStateException("Not implemented");
    }

    default void taiyitist$setCraftLootTable(CraftLootTable craftLootTable) {
        throw new IllegalStateException("Not implemented");
    }
}
