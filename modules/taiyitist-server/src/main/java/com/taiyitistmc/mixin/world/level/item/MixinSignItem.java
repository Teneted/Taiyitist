package com.taiyitistmc.mixin.world.level.item;

import com.taiyitistmc.asm.annotation.TransformAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.SignItem;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SignItem.class)
public class MixinSignItem {

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static BlockPos openSign; // CraftBukkit

}
