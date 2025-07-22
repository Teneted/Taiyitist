package com.taiyitistmc.mixin;

import com.taiyitistmc.TaiyitistMCStart;
import com.taiyitistmc.asm.CreateConstructorProcessor;
import com.taiyitistmc.asm.InlineFieldProcessor;
import com.taiyitistmc.asm.InlineMethodProcessor;
import com.taiyitistmc.asm.InvokeSpecialProcessor;
import com.taiyitistmc.asm.MixinProcessor;
import com.taiyitistmc.asm.RenameIntoProcessor;
import com.taiyitistmc.asm.ShouldApplyProcessor;
import com.taiyitistmc.asm.TransformAccessProcessor;
import io.izzel.arclight.mixin.MixinTools;
import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IEnvironmentTokenProvider;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class TaiyitistMixinPlugin implements IMixinConfigPlugin, IEnvironmentTokenProvider {

    private final List<MixinProcessor> preProcessors = List.of(
    );

    private final List<MixinProcessor> postProcessors = List.of(
            new RenameIntoProcessor(),
            new TransformAccessProcessor(),
            new CreateConstructorProcessor(),
            new InlineMethodProcessor(),
            new InlineFieldProcessor(),
            new InvokeSpecialProcessor()
    );

    @Override
    public void onLoad(String mixinPackage) {
        MixinTools.setup();
        MixinEnvironment.getCurrentEnvironment().registerTokenProvider(this);
        try {
            TaiyitistMCStart.run();
        } catch (Exception ex) {
            TaiyitistMCStart.LOGGER.error("Failed to load Taiyitist Server..., caused by " + ex.getCause());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // Taiyitist start - compat for arclight
        if (FabricLoader.getInstance().isModLoaded("arclight")) {
            return false;
        }
        // Taiyitist end
        if (mixinClassName.equals("com.taiyitistmc.mixin.core.world.entity.MixinMob$PaperSpawnAffect")) {
            return !FabricLoader.getInstance().isModLoaded("vmp");
        }
        if (mixinClassName.equals("com.taiyitistmc.mixin.core.world.level.spawner.MixinNaturalSpawner")) {
            return !FabricLoader.getInstance().isModLoaded("carpet-tis-addition")
                    && !FabricLoader.getInstance().isModLoaded("carpet");
        }
        if (mixinClassName.equals("com.taiyitistmc.mixin.core.network.protocol.MixinPacketUtils")) {
            return !FabricLoader.getInstance().isModLoaded("cobblemon");
        }
        if (mixinClassName.equals("com.taiyitistmc.mixin.core.world.item.MixinChorusFruitItem")) {
            return !FabricLoader.getInstance().isModLoaded("openpartiesandclaims");
        }
        if (mixinClassName.equals("com.taiyitistmc.mixin.server.commands.MixinWorldBorderCommand")) {
            return !FabricLoader.getInstance().isModLoaded("multiworldborders");
        }
        return ShouldApplyProcessor.shouldApply(mixinClassName);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        for (var processor : this.preProcessors) {
            processor.accept(targetClassName, targetClass, mixinInfo);
        }
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        for (var processor : this.postProcessors) {
            processor.accept(targetClassName, targetClass, mixinInfo);
        }
        MixinTools.onPostMixin(targetClass);
    }

    @Override
    public int getPriority() {
        return 500;
    }

    @Override
    public Integer getToken(String token, MixinEnvironment env) {
        return null;
    }
}
