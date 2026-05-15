package org.teneted.taiyitist.mixin.world.level.block;

import net.minecraft.world.level.block.Block;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.asm.annotation.TransformAccess;
import org.teneted.taiyitist.injection.world.level.block.InjectionBlock;

@Mixin(Block.class)
public class MixinBlock implements InjectionBlock {

    // Spigot start
    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static float range(float min, float value, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}
