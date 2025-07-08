package com.taiyitistmc.mixin.world.level.block;

import com.taiyitistmc.asm.annotation.TransformAccess;
import net.minecraft.world.level.block.DispenserBlock;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DispenserBlock.class)
public class MixinDispenserBlock {

    @TransformAccess(Opcodes.ACC_STATIC | Opcodes.ACC_FINAL)
    private static boolean eventFired = false; // CraftBukkit
}
