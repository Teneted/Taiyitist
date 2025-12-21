package org.celestial_artistry.taiyitist.mixin.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.ComposterBlock;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftBlockInventoryHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ComposterBlock.EmptyContainer.class)
public class MixinComposterBlock_EmptyContainer extends SimpleContainer {

    @Unique
    public void taiyitist$constructor() {
        throw new RuntimeException();
    }

    @Unique
    public void taiyitist$constructor(LevelAccessor world, BlockPos blockPos) {
        taiyitist$constructor();
        this.setOwner(new CraftBlockInventoryHolder(world, blockPos, this));
    }
}
