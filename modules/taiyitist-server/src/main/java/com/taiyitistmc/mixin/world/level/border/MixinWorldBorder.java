package com.taiyitistmc.mixin.world.level.border;

import com.taiyitistmc.injection.world.level.border.InjectionWorldBorder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WorldBorder.class)
public class MixinWorldBorder implements InjectionWorldBorder {

    @Shadow @Final private List<BorderChangeListener> listeners;
    public net.minecraft.world.level.Level world; // CraftBukkit

    @Inject(method = "addListener", at = @At("HEAD"), cancellable = true)
    private void taiyitist$checkBorder(BorderChangeListener borderChangeListener, CallbackInfo ci) {
        if (listeners.contains(borderChangeListener)) ci.cancel(); // CraftBukkit
    }

    @Override
    public Level bridge$world() {
        return world;
    }

    @Override
    public void taiyitist$setWorld(Level world) {
        this.world = world;
    }
}
