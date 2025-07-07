package com.taiyitistmc.mixin.world.level.block.entity;

import com.taiyitistmc.injection.world.level.block.entity.InjectionCatalystListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.entity.SculkCatalystBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SculkCatalystBlockEntity.CatalystListener.class)
public class MixinSculkCatalystBlockEntity_CatalystListener implements InjectionCatalystListener {

    @Shadow @Final
    SculkSpreader sculkSpreader;

    @Override
    public void taiyitist$setLevel(Level level) {
        this.sculkSpreader.taiyitist$setLevel(level);
    }
}
