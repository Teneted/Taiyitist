package com.taiyitistmc.mixin.world.level.dimension.end;

import com.llamalad7.mixinextras.sugar.Local;
import com.taiyitistmc.injection.world.level.dimension.end.InjectionEndDragonFight;
import java.util.List;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EndDragonFight.class)
public class MixinEndDragonFight implements InjectionEndDragonFight {

    @Shadow
    @Final
    public ServerBossEvent dragonEvent;
    public boolean taiyitist$respawnDragon = false;

    @Inject(method = "respawnDragon",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/world/level/dimension/end/EndDragonFight;respawnCrystals:Ljava/util/List;",
                    shift = At.Shift.AFTER))
    private void taiyitist$setRespawnResult(List<EndCrystal> crystals, CallbackInfo ci) {
        taiyitist$respawnDragon = true;
    }

    @Inject(method = "scanState", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;discard()V")
    )
    private void taiyitist$pushNullReason(CallbackInfo ci, @Local EnderDragon enderDragon) {
        enderDragon.pushRemoveCause(null); // CraftBukkit - add Bukkit remove cause
    }

    @Override
    public boolean bridge$isRespawnDragon() {
        return taiyitist$respawnDragon;
    }
}
