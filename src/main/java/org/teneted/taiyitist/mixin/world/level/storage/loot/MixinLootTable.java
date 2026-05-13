package org.teneted.taiyitist.mixin.world.level.storage.loot;

import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.storage.loot.InjectionLootTable;

@Mixin(LootTable.class)
public class MixinLootTable implements InjectionLootTable {
}
