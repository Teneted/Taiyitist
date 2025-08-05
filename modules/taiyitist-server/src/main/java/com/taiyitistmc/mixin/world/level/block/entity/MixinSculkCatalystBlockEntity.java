package com.taiyitistmc.mixin.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SculkCatalystBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SculkCatalystBlockEntity.class)
public abstract class MixinSculkCatalystBlockEntity extends BlockEntity {

    @Shadow
    @Final
    private SculkCatalystBlockEntity.CatalystListener catalystListener;

    public MixinSculkCatalystBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void taiyitist$init(BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
        catalystListener.taiyitist$setLevel(level); // CraftBukkit
    }

    @Inject(method = "serverTick", at = @At("HEAD"))
    private static void taiyitist$overrideSource(Level p_222780_, BlockPos p_222781_, BlockState p_222782_, SculkCatalystBlockEntity blockEntity, CallbackInfo ci) {
        CraftEventFactory.sourceBlockOverride = blockEntity.getBlockPos();
    }

    @Inject(method = "serverTick", at = @At("RETURN"))
    private static void taiyitist$resetSource(Level p_222780_, BlockPos p_222781_, BlockState p_222782_, SculkCatalystBlockEntity blockEntity, CallbackInfo ci) {
        CraftEventFactory.sourceBlockOverride = null;
    }

    @Inject(method = "loadAdditional", at = @At("HEAD"))
    private void taiyitist$load(ValueInput valueInput, CallbackInfo ci) {
        super.loadAdditional(valueInput); // CraftBukkit - SPIGOT-7393: Load super Bukkit data
    }
}
