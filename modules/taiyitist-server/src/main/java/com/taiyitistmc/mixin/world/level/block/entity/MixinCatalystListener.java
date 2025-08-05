package com.taiyitistmc.mixin.world.level.block.entity;

import com.taiyitistmc.injection.world.level.block.entity.InjectionCatalystListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SculkCatalystBlockEntity;
import org.checkerframework.common.aliasing.qual.Unique;
import org.spongepowered.asm.mixin.Mixin;

// MixinSculkCatalystBlockEntity_CatalystListener.java
@Mixin(SculkCatalystBlockEntity.CatalystListener.class)
public abstract class MixinCatalystListener implements InjectionCatalystListener {
    @Unique
    private Level taiyitist$level;

    @Override
    public void taiyitist$setLevel(Level level) {
        this.taiyitist$level = level; // 实际逻辑按需修改
    }
}
