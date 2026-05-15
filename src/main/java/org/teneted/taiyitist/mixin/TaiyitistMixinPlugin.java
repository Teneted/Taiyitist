package org.teneted.taiyitist.mixin;

import io.izzel.arclight.mixin.MixinTools;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.teneted.taiyitist.asm.CreateConstructorProcessor;
import org.teneted.taiyitist.asm.InlineFieldProcessor;
import org.teneted.taiyitist.asm.InlineMethodProcessor;
import org.teneted.taiyitist.asm.InvokeSpecialProcessor;
import org.teneted.taiyitist.asm.MixinProcessor;
import org.teneted.taiyitist.asm.RenameIntoProcessor;
import org.teneted.taiyitist.asm.TransformAccessProcessor;

import java.util.List;
import java.util.Set;

public class TaiyitistMixinPlugin implements IMixinConfigPlugin {

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

    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
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
}
