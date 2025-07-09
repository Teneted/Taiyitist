package com.taiyitistmc.mixin.world.level.levelgen.structure.templatesystem;

import com.taiyitistmc.injection.world.level.levelgen.structure.templatesystem.InjectionStructureTemplateManager;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StructureTemplateManager.class)
public abstract class MixinStructureTemplateManager implements InjectionStructureTemplateManager {

    @Shadow
    protected abstract Optional<StructureTemplate> loadFromResource(ResourceLocation id);

    // Taiyitist start Fix modernfix mod
    @Override
    public Optional<StructureTemplate> loadFromResource0(ResourceLocation id) {
        return loadFromResource(id);
    }
    // Taiyitist end

}
