package org.teneted.taiyitist.mixin.world.level.levelgen;

import org.teneted.taiyitist.injection.world.level.levelgen.InjectionFlatLevelSource;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(FlatLevelSource.class)
public abstract class MixinFlatLevelSource implements InjectionFlatLevelSource {

    @Mutable
    @Shadow @Final private FlatLevelGeneratorSettings settings;

    @Unique
    private BiomeSource taiyitist$biomeSource;

    @Unique
    public void taiyitist$constructor$super(BiomeSource biomeSource,
                                         Function<Holder<Biome>, BiomeGenerationSettings> generationSettingsGetter, BiomeSource newBiomeSource) {
        throw new RuntimeException();
    }

    @Unique
    public void taiyitist$constructor(FlatLevelGeneratorSettings settings, BiomeSource biomeSource) {
        taiyitist$constructor$super(biomeSource, Util.memoize(settings::adjustGenerationSettings), biomeSource);
        taiyitist$biomeSource = taiyitist$biomeSource == null ? new FixedBiomeSource(settings.getBiome()) : taiyitist$biomeSource;
        biomeSource = taiyitist$biomeSource;
        this.settings = settings;
    }

    @Override
    public void taiyitist$setBiomeSource(BiomeSource biomeSource) {
        this.taiyitist$biomeSource = biomeSource;
    }
}
