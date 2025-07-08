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

    private transient Level taiyitist$level;

    @Shadow
    public abstract boolean isWorldGeneration();

    @Override
    public void taiyitist$setLevel(Level level) {
        this.taiyitist$level = level;
    }

    @Inject(method = "addCursor", cancellable = true, at = @At(value = "INVOKE", remap = false, target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private void taiyitist$bloomEvent(SculkSpreader.ChargeCursor cursor, CallbackInfo ci) {
        if (!isWorldGeneration() && taiyitist$level != null) {
            var bukkitBlock = CraftBlock.at(taiyitist$level, cursor.pos);
            var event = new SculkBloomEvent(bukkitBlock, cursor.getCharge());
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                ci.cancel();
            }
            cursor.charge = event.getCharge();
        }
    }
}