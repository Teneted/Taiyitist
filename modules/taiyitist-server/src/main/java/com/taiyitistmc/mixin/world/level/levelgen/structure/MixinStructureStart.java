package com.taiyitistmc.mixin.world.level.levelgen.structure;

import com.taiyitistmc.injection.world.level.levelgen.structure.InjectionStructureStart;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.bukkit.craftbukkit.persistence.DirtyCraftPersistentDataContainer;
import org.bukkit.event.world.AsyncStructureGenerateEvent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StructureStart.class)
public abstract class MixinStructureStart implements InjectionStructureStart {

    // CraftBukkit start
    private static final org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry DATA_TYPE_REGISTRY = new org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry();
    public DirtyCraftPersistentDataContainer persistentDataContainer = new DirtyCraftPersistentDataContainer(DATA_TYPE_REGISTRY);
    public AsyncStructureGenerateEvent.Cause generationEventCause = AsyncStructureGenerateEvent.Cause.WORLD_GENERATION;
    // CraftBukkit end

    @Override
    public AsyncStructureGenerateEvent.Cause bridge$generationEventCause() {
        return generationEventCause;
    }

    @Override
    public DirtyCraftPersistentDataContainer bridge$persistentDataContainer() {
        return persistentDataContainer;
    }

    @Override
    public void taiyitist$setGenerationEventCause(AsyncStructureGenerateEvent.Cause generationEventCause) {
        this.generationEventCause = generationEventCause;
    }

    @Override
    public void taiyitist$setPersistentDataContainer(DirtyCraftPersistentDataContainer persistentDataContainer) {
        this.persistentDataContainer = persistentDataContainer;
    }
}
