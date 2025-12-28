package org.teneted.taiyitist.mixin.world.level.lighting;

import org.teneted.taiyitist.config.TaiyitistConfig;
import net.minecraft.world.level.lighting.SkyLightSectionStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SkyLightSectionStorage.class)
public class MixinSkyLightSectionStorage {

    /**
     * @author Mgazul
     * @reason Mandatory 15
     */
    @Inject(method = "getLightValue(JZ)I", cancellable = true, at = @At(value = "HEAD"))
    protected void getLightValue(long packedPos, boolean updateAll, CallbackInfoReturnable<Integer> cir) {
        if (TaiyitistConfig.SkyLight15) {
            cir.setReturnValue(15);
            cir.cancel();
        }
    }
}
