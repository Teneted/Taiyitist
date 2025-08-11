package com.taiyitistmc.paper.mixin;

import com.taiyitistmc.asm.CreateConstructorProcessor;
import com.taiyitistmc.asm.InlineFieldProcessor;
import com.taiyitistmc.asm.InlineMethodProcessor;
import com.taiyitistmc.asm.InvokeSpecialProcessor;
import com.taiyitistmc.asm.MixinProcessor;
import com.taiyitistmc.asm.RenameIntoProcessor;
import com.taiyitistmc.asm.ShouldApplyProcessor;
import com.taiyitistmc.asm.TransformAccessProcessor;
import io.izzel.arclight.mixin.MixinTools;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IEnvironmentTokenProvider;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class TaiyitistPaperMixinPlugin implements IMixinConfigPlugin, IEnvironmentTokenProvider {

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
        MixinEnvironment.getCurrentEnvironment().registerTokenProvider(this);
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
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
