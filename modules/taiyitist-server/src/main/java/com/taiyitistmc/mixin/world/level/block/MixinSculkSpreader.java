package com.taiyitistmc.mixin.world.level.block;

import com.taiyitistmc.injection.world.level.block.InjectionSculkSpreader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkSpreader;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.block.SculkBloomEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SculkSpreader.class)
public abstract class MixinSculkSpreader implements InjectionSculkSpreader {

    @Shadow public abstract boolean isWorldGeneration();

    public Level level; // CraftBukkit

    @Inject(method = "addCursor", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), cancellable = true)
    private void taiyitist$callSculkBloomEvent(SculkSpreader.ChargeCursor chargeCursor, CallbackInfo ci) {
        // CraftBukkit start
        if (!isWorldGeneration()) { // CraftBukkit - SPIGOT-7475: Don't call event during world generation
            CraftBlock bukkitBlock = CraftBlock.at(level, chargeCursor.pos);
            SculkBloomEvent event = new SculkBloomEvent(bukkitBlock, chargeCursor.getCharge());
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                ci.cancel();
                return;
            }

            chargeCursor.charge = event.getCharge();
        }
        // CraftBukkit end
    }

    @Override
    public Level bridge$level() {
        return this.level;
    }

    @Override
    public void taiyitist$setLevel(Level level) {
        this.level = level;
    }
}
