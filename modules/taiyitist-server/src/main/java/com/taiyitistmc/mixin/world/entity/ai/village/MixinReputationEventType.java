package com.taiyitistmc.mixin.world.entity.ai.village;

import net.minecraft.world.entity.ai.village.ReputationEventType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ReputationEventType.class)
public interface MixinReputationEventType extends ReputationEventTypeAddon {

    @Shadow
    static ReputationEventType register(String string) {
        return null;
    }

    @Mixin(targets = "net.minecraft.world.entity.ai.village.ReputationEventType$1")
    class MixinReputationEventType_1 {

        @Shadow @Final
        String val$name;

        @Inject(method = "toString", at = @At("HEAD"))
        private void taiyitst$addNewMap(CallbackInfoReturnable<String> cir) {
            // CraftBukkit start - add new value to map
            {
                BY_ID.put(val$name, ((ReputationEventType) (Object) this));
            }
            // CraftBukkit end
        }
    }
}
