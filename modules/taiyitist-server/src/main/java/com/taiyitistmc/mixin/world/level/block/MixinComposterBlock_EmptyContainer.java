package com.taiyitistmc.mixin.world.level.block;

import com.taiyitistmc.asm.annotation.CreateConstructor;
import com.taiyitistmc.asm.annotation.ShadowConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.ComposterBlock;
import org.bukkit.craftbukkit.inventory.CraftBlockInventoryHolder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ComposterBlock.EmptyContainer.class)
public class MixinComposterBlock_EmptyContainer extends SimpleContainer {

    @ShadowConstructor
    public void taiyitist$constructor() {
        throw new RuntimeException();
    }

    @CreateConstructor
    public void taiyitist$constructor(LevelAccessor world, BlockPos blockPos) {
        taiyitist$constructor();
        this.setOwner(new CraftBlockInventoryHolder(world, blockPos, this));
    }
}
