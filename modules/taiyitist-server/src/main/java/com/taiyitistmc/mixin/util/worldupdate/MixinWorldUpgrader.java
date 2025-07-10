package com.taiyitistmc.mixin.util.worldupdate;

import com.mojang.datafixers.DataFixer;
import com.taiyitistmc.injection.world.level.storage.InjectionLevelStorageAccess;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.stream.Collectors;


@Mixin(WorldUpgrader.class)
public abstract class MixinWorldUpgrader implements AutoCloseable {


    @Mutable
    @Shadow @Final
    Set<ResourceKey<Level>> levels;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void taiyitist$init(LevelStorageSource.LevelStorageAccess levelStorageAccess,
                                DataFixer dataFixer, WorldData worldData, RegistryAccess registryAccess,
                                boolean bl, boolean bl2, CallbackInfo ci) {
        this.levels = (Set) java.util.stream.Stream.of(((InjectionLevelStorageAccess) levelStorageAccess).bridge$getTypeKey()).map(Registries::levelStemToLevel).collect(Collectors.toUnmodifiableSet()); // CraftBukkit
    }
}
