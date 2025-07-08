package com.taiyitistmc.mixin.world.level.levelgen;

import com.taiyitistmc.asm.annotation.CreateConstructor;
import com.taiyitistmc.asm.annotation.ShadowConstructor;
import com.taiyitistmc.injection.world.level.levelgen.InjectionFlatLevelSource;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FlatLevelSource.class)
public abstract class MixinFlatLevelSource implements InjectionFlatLevelSource {

    @Mutable
    @Shadow
    @Final
    private FlatLevelGeneratorSettings settings;

    private BiomeSource taiyitist$biomeSource;

    @ShadowConstructor
    public void taiyitist$constructor$super(FlatLevelGeneratorSettings flatLevelGeneratorSettings) {
        throw new RuntimeException();
    }

    @CreateConstructor
    public void taiyitist$constructor(FlatLevelGeneratorSettings settings, BiomeSource biomeSource) {
        taiyitist$constructor$super(settings);
        taiyitist$biomeSource = taiyitist$biomeSource == null ? new FixedBiomeSource(settings.getBiome()) : taiyitist$biomeSource;
        this.settings = settings;
    }

    @Override
    public void taiyitist$setBiomeSource(BiomeSource biomeSource) {
        this.taiyitist$biomeSource = biomeSource;
    }
}
