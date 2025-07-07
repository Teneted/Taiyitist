package com.taiyitistmc.mixin.interaction;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MappedRegistry.class)
public abstract class MixinMappedRegistry<T> implements WritableRegistry<T> {

    public final Map<ResourceLocation, T> temporaryUnfrozenMap = new HashMap<>(); // Paper - support pre-filling in registry mod API

    @Inject(method = "register", at = @At("RETURN"))
    private void taiyitist$register(ResourceKey<T> resourceKey, T object, RegistrationInfo registrationInfo, CallbackInfoReturnable<Holder.Reference<T>> cir) {
        this.temporaryUnfrozenMap.put(resourceKey.location(), object); // Paper - support pre-filling in registry mod API
    }
}
