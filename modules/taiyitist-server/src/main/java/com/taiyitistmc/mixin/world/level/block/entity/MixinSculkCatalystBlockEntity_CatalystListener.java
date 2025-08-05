package com.taiyitistmc.mixin.world.level.block.entity;

import com.taiyitistmc.injection.world.level.block.entity.InjectionCatalystListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.entity.SculkCatalystBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.PositionSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SculkCatalystBlockEntity.CatalystListener.class)
public class MixinSculkCatalystBlockEntity_CatalystListener implements InjectionCatalystListener {

    @Shadow
    @Final
    SculkSpreader sculkSpreader;
    private Level level; // CraftBukkit

    @Inject(method = "<init>", at = @At("RETURN"))
    private void taiyitist$init(BlockState blockState, PositionSource positionSource, CallbackInfo ci) {
        this.sculkSpreader.taiyitist$setLevel(level);// CraftBukkit
    }

    @Override
    public Level bridge$level() {
        return this.level;
    }

    @Override
    public void taiyitist$setLevel(Level level) {
        this.level = level;
    }
}
