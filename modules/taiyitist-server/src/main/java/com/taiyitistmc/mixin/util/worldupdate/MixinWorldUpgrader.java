package com.taiyitistmc.mixin.util.worldupdate;

import com.mojang.datafixers.DataFixer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.stream.Collectors;


@Mixin(WorldUpgrader.class)
public class MixinWorldUpgrader {

    @Shadow
    @Final
    private HolderLookup<LevelStem> dimensions;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(LevelStorageSource.LevelStorageAccess session,
                        DataFixer dataFixer,
                        WorldData saveData,
                        RegistryAccess registryAccess,
                        boolean eraseCache,
                        boolean flag1,
                        CallbackInfo ci) {

        Set<ResourceKey<Level>> newLevels = this.dimensions.listElements().map(holder ->
                Registries.levelStemToLevel(holder.key())
        ).collect(Collectors.toUnmodifiableSet());

        ((WorldUpgraderAccessor) this).setLevels(newLevels);
    }
}
