package com.taiyitistmc.mixin.core.component;

import com.taiyitistmc.injection.network.chat.InjectionDataComponentPatch;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import java.util.Optional;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DataComponentPatch.Builder.class)
public class MixinDataComponentPatch_Builder implements InjectionDataComponentPatch {

    @Shadow
    @Final
    public Reference2ObjectMap<DataComponentType<?>, Optional<?>> map;

    @Override
    public void copy(DataComponentPatch orig) {
        this.map.putAll(orig.map);
    }

    @Override
    public void clear(DataComponentType<?> type) {
        this.map.remove(type);
    }

    @Override
    public boolean isSet(DataComponentType<?> type) {
        return map.containsKey(type);
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object instanceof DataComponentPatch.Builder patch) {
            return this.map.equals(patch.map);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.map.hashCode();
    }
}